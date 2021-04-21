package ch.obermuhlner.game.chess;

import org.junit.Test;

public class SyzygyRestLookupTableTest {

    @Test
    public void testLookup() {
        SyzygyRestLookupTable lookupTable = new SyzygyRestLookupTable();
        String move = lookupTable.bestMove("4k3/6KP/8/8/8/8/7p/8_w_-_-_0_1");
        System.out.println(move);
    }
}
