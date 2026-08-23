package it.niedermann.nextcloud.deck.domain.e2e.usecases.labels;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import it.niedermann.nextcloud.deck.domain.e2e.EndToEndTest;
import it.niedermann.nextcloud.deck.domain.e2e.EndToEndUtil;
import it.niedermann.nextcloud.deck.domain.model.Board;
import it.niedermann.nextcloud.deck.domain.model.Color;
import it.niedermann.nextcloud.deck.domain.model.Label;

public class LabelEndToEndTest extends EndToEndTest {

    private VirtualDeviceAndAccount DEVICE_A;
    private VirtualDeviceAndAccount DEVICE_B;
    private Board boardA;

    @BeforeEach
    @Override
    public void setup() throws IOException {
        super.setup();

        DEVICE_A = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");
        DEVICE_B = getOrCreateRemoteAccountAndImport(createVirtualDevice(), "johndoe");

        final var boardTitle = randomUtil.randomize("boardForLabel");
        boardA = EndToEndUtil.createBoard(DEVICE_A, boardTitle);
    }

    @Test
    public void createLabel() {
        final var labelTitle = randomUtil.randomize("createLabel");
        final var label = new Label(new Label.ID(0), boardA.id(), labelTitle, new Color(255, 0, 0));

        DEVICE_A.virtualDevice().getAddLabelUseCase().execute(label).join();

        EndToEndUtil.assertLabelExists(DEVICE_A, boardA, labelTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertLabelExists(DEVICE_B, boardA, labelTitle);
    }

    @Test
    public void updateLabel() {
        final var labelTitle = randomUtil.randomize("labelToUpdate");
        final var label = EndToEndUtil.createLabel(DEVICE_A, boardA, labelTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        EndToEndUtil.assertLabelExists(DEVICE_B, boardA, labelTitle);

        final var newTitle = randomUtil.randomize("updatedLabel");
        final var updatedLabel = new Label(label.id(), label.boardId(), newTitle, label.color());

        DEVICE_A.virtualDevice().getUpdateLabelUseCase().execute(updatedLabel).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertLabelExists(DEVICE_A, boardA, newTitle);
        EndToEndUtil.assertLabelExists(DEVICE_B, boardA, newTitle);
        EndToEndUtil.assertLabelDoesNotExist(DEVICE_B, boardA, labelTitle);
    }

    @Test
    public void deleteLabel() {
        final var labelTitle = randomUtil.randomize("labelToDelete");
        final var label = EndToEndUtil.createLabel(DEVICE_A, boardA, labelTitle);

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);
        EndToEndUtil.assertLabelExists(DEVICE_B, boardA, labelTitle);

        DEVICE_A.virtualDevice().getDeleteLabelUseCase().execute(label.id()).join();

        synchronize(DEVICE_A);
        synchronize(DEVICE_B);

        EndToEndUtil.assertLabelDoesNotExist(DEVICE_A, boardA, labelTitle);
        EndToEndUtil.assertLabelDoesNotExist(DEVICE_B, boardA, labelTitle);
    }
}
