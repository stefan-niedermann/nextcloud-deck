package it.niedermann.nextcloud.deck.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

@Entity
public class JoinBoardWithLabel {
    @Id
    private Long id;
    private Long labelId;
    private Long boardId;

    @Generated(hash = 606733805)
    public JoinBoardWithLabel(Long id, Long labelId, Long boardId) {
        this.id = id;
        this.labelId = labelId;
        this.boardId = boardId;
    }

    @Generated(hash = 371373828)
    public JoinBoardWithLabel() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getLabelId() {
        return labelId;
    }

    public void setLabelId(Long labelId) {
        this.labelId = labelId;
    }

    public Long getBoardId() {
        return boardId;
    }

    public void setBoardId(Long boardId) {
        this.boardId = boardId;
    }
}
