package it.niedermann.nextcloud.deck.ui.board.accesscontrol;

import it.niedermann.nextcloud.deck.model.AccessControl;

public interface AccessControlChangedListener {
        void updateAccessControl(AccessControl accessControl);

        void deleteAccessControl(AccessControl ac);
    }