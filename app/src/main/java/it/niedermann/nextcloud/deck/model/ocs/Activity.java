package it.niedermann.nextcloud.deck.model.ocs;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;

import it.niedermann.nextcloud.deck.model.Account;
import it.niedermann.nextcloud.deck.model.Card;
import it.niedermann.nextcloud.deck.model.interfaces.AbstractRemoteEntity;


@Entity(inheritSuperIndices = true,
        indices = {
                @Index(value = "accountId", name = "activity_accID"),
                @Index(value = "cardId", name = "activity_cardID")
        },
        foreignKeys = {
                @ForeignKey(
                        entity = Card.class,
                        parentColumns = "localId",
                        childColumns = "cardId", onDelete = ForeignKey.CASCADE
                ),
                @ForeignKey(
                        entity = Account.class,
                        parentColumns = "id",
                        childColumns = "accountId", onDelete = ForeignKey.CASCADE
                )
        }
)
public class Activity extends AbstractRemoteEntity {

    private long cardId;
    private String subject;
    private int type;

    public long getCardId() {
        return cardId;
    }

    public void setCardId(long cardId) {
        this.cardId = cardId;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

//    {
//        "ocs": {
//        "meta": {
//            "status": "ok",
//                    "statuscode": 200,
//                    "message": "OK"
//        },
//        "data": [
//        {
//            "
//            ": 29067,
//                "app": "deck",
//                "type": "deck",
//                "user": "artur",
//                "subject": "You have created card test 3 in stack asdf on board Deck app",
//                "subject_rich": [
//            "You have created card {card} in stack {stack} on board {board}",
//                    {
//                            "user": {
//            "type": "user",
//                    "id": "artur",
//                    "name": "artur"
//        },
//            "card": {
//            "type": "highlight",
//                    "id": 156,
//                    "name": "test 3",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90//card/156"
//        },
//            "board": {
//            "type": "highlight",
//                    "id": 90,
//                    "name": "Deck app",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90/"
//        },
//            "stack": {
//            "type": "highlight",
//                    "id": 447,
//                    "name": "asdf"
//        }
//          }
//        ],
//            "message": "",
//                "message_rich": [
//            "",
//          []
//        ],
//            "object_type": "deck_card",
//                "object_id": 156,
//                "object_name": "test 3",
//                "objects": {
//            "156": "test 3"
//        },
//            "link": "",
//                "icon": "/apps/files/img/add-color.svg",
//                "datetime": "2019-09-16T15:47:07+00:00"
//        },
//        {
//            "activity_id": 29833,
//                "app": "deck",
//                "type": "deck",
//                "user": "artur",
//                "subject": "You have moved the card test 3 from stack asdf to asdf",
//                "subject_rich": [
//            "You have moved the card {card} from stack {stackBefore} to {stack}",
//                    {
//                            "user": {
//            "type": "user",
//                    "id": "artur",
//                    "name": "artur"
//        },
//            "card": {
//            "type": "highlight",
//                    "id": 156,
//                    "name": "test 3",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90//card/156"
//        },
//            "board": {
//            "type": "highlight",
//                    "id": 90,
//                    "name": "Deck app",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90/"
//        },
//            "stack": {
//            "type": "highlight",
//                    "id": 447,
//                    "name": "asdf"
//        },
//            "stackBefore": {
//            "type": "highlight",
//                    "id": 447,
//                    "name": "asdf"
//        },
//            "before": {
//            "type": "highlight",
//                    "id": 447,
//                    "name": 447
//        },
//            "after": {
//            "type": "highlight",
//                    "id": 460,
//                    "name": 460
//        }
//          }
//        ],
//            "message": "",
//                "message_rich": [
//            "",
//          []
//        ],
//            "object_type": "deck_card",
//                "object_id": 156,
//                "object_name": "test 3",
//                "objects": {
//            "156": "test 3"
//        },
//            "link": "",
//                "icon": "/apps/files/img/change.svg",
//                "datetime": "2019-09-17T10:41:37+00:00"
//        },
//        {
//            "activity_id": 29834,
//                "app": "deck",
//                "type": "deck_card_description",
//                "user": "artur",
//                "subject": "You have updated the description of card test 3 in stack asdfd on board Deck app",
//                "subject_rich": [
//            "You have updated the description of card {card} in stack {stack} on board {board}",
//                    {
//                            "user": {
//            "type": "user",
//                    "id": "artur",
//                    "name": "artur"
//        },
//            "card": {
//            "type": "highlight",
//                    "id": 156,
//                    "name": "test 3",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90//card/156"
//        },
//            "board": {
//            "type": "highlight",
//                    "id": 90,
//                    "name": "Deck app",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90/"
//        },
//            "stack": {
//            "type": "highlight",
//                    "id": 460,
//                    "name": "asdfd"
//        }
//          }
//        ],
//            "message": "<pre class=\"visualdiff\"></pre>",
//                "message_rich": [
//            "",
//          []
//        ],
//            "object_type": "deck_card",
//                "object_id": 156,
//                "object_name": "test 3",
//                "objects": {
//            "156": "test 3"
//        },
//            "link": "",
//                "icon": "/apps/files/img/change.svg",
//                "datetime": "2019-09-17T10:45:32+00:00"
//        },
//        {
//            "activity_id": 29835,
//                "app": "deck",
//                "type": "deck",
//                "user": "artur",
//                "subject": "You have added the tag To review to card test 3 in stack asdfd on board Deck app",
//                "subject_rich": [
//            "You have added the tag {label} to card {card} in stack {stack} on board {board}",
//                    {
//                            "user": {
//            "type": "user",
//                    "id": "artur",
//                    "name": "artur"
//        },
//            "card": {
//            "type": "highlight",
//                    "id": 156,
//                    "name": "test 3",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90//card/156"
//        },
//            "board": {
//            "type": "highlight",
//                    "id": 90,
//                    "name": "Deck app",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90/"
//        },
//            "stack": {
//            "type": "highlight",
//                    "id": 460,
//                    "name": "asdfd"
//        },
//            "label": {
//            "type": "highlight",
//                    "id": 397,
//                    "name": "To review"
//        }
//          }
//        ],
//            "message": "",
//                "message_rich": [
//            "",
//          []
//        ],
//            "object_type": "deck_card",
//                "object_id": 156,
//                "object_name": "test 3",
//                "objects": {
//            "156": "test 3"
//        },
//            "link": "",
//                "icon": "/apps/deck/img/deck-dark.svg",
//                "datetime": "2019-09-17T10:47:20+00:00"
//        },
//        {
//            "activity_id": 29836,
//                "app": "deck",
//                "type": "deck_card_description",
//                "user": "artur",
//                "subject": "You have updated the description of card test 3 in stack asdfd on board Deck app",
//                "subject_rich": [
//            "You have updated the description of card {card} in stack {stack} on board {board}",
//                    {
//                            "user": {
//            "type": "user",
//                    "id": "artur",
//                    "name": "artur"
//        },
//            "card": {
//            "type": "highlight",
//                    "id": 156,
//                    "name": "test 3",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90//card/156"
//        },
//            "board": {
//            "type": "highlight",
//                    "id": 90,
//                    "name": "Deck app",
//                    "link": "https://nextcloud.niedermann.it/index.php/apps/deck/#!/board/90/"
//        },
//            "stack": {
//            "type": "highlight",
//                    "id": 460,
//                    "name": "asdfd"
//        }
//          }
//        ],
//            "message": "<pre class=\"visualdiff\"><ins>great description.</ins></pre>",
//                "message_rich": [
//            "",
//          []
//        ],
//            "object_type": "deck_card",
//                "object_id": 156,
//                "object_name": "test 3",
//                "objects": {
//            "156": "test 3"
//        },
//            "link": "",
//                "icon": "/apps/files/img/change.svg",
//                "datetime": "2019-09-17T10:50:13+00:00"
//        }
//    ]
//    }
//    }

}
