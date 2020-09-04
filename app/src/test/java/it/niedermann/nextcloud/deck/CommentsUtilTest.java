package it.niedermann.nextcloud.deck;

import org.junit.Assert;
import org.junit.Test;

import it.niedermann.nextcloud.deck.ui.card.comments.util.CommentsUtil;

public class CommentsUtilTest {

    @Test
    public void testMentionDiscovery() {
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("", 0));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("a ", 1));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("a ", 2));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("@a ", 3));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("d@a", 2));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("d@a", 0));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("d@a", 3));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("ab", 0));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("ab", 1));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("ab", 2));
        Assert.assertNull(CommentsUtil.getUserNameForMentionProposal("def @ab ", 8));
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("\n@ab", 3).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("\n@ab", 4).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("\t@ab", 3).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal(" @ab", 3).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("@ab", 3).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("@ab", 2).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("@ab def", 2).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("@ab def", 3).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("def @ab ", 5).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("def @ab", 6).first);
        Assert.assertEquals("ab_asdf_ldklsdkf", CommentsUtil.getUserNameForMentionProposal("def @ab_asdf_ldklsdkf", 7).first);
        Assert.assertEquals("ab_asdf_ldklsdkf", CommentsUtil.getUserNameForMentionProposal("def @ab_asdf_ldklsdkf   ", 7).first);
        Assert.assertEquals("\"ab_asdf_ldklsdkf\"", CommentsUtil.getUserNameForMentionProposal("def @\"ab_asdf_ldklsdkf\"", 7).first);
        Assert.assertEquals("\"ab_asdf_ldklsdkf\"", CommentsUtil.getUserNameForMentionProposal("def @\"ab_asdf_ldklsdkf\" \nasdf", 7).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("def @ab\n", 7).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("\ndef @ab\n", 8).first);
        Assert.assertEquals("ab", CommentsUtil.getUserNameForMentionProposal("\n def @ab\nasdfasdf", 9).first);
    }
}
