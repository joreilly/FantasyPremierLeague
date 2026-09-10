package dev.johnoreilly.common.format

import dev.johnoreilly.common.model.Player
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class PlayerCsvTest {

    @Test
    fun `toFixed renders a fixed number of decimals`() {
        assertEquals("6.0", 6.0.toFixed(1))
        assertEquals("7.2", 7.2.toFixed(1))
        assertEquals("12.0", 12.0.toFixed(1))
        assertEquals("0.56", 0.56.toFixed(2))
        assertEquals("1.02", 1.02.toFixed(2))
        assertEquals("0.02", 0.02.toFixed(2))
        assertEquals("50", 50.0.toFixed(0))
        assertEquals("0.0", 0.0.toFixed(1))
    }

    @Test
    fun `toFixed keeps the sign on negative values`() {
        // Form and points both go negative (red cards, own goals), and a mangled sign would
        // read as a good player rather than a bad one.
        assertEquals("-0.7", (-0.7).toFixed(1))
        assertEquals("-2.0", (-2.0).toFixed(1))
        assertEquals("-0.05", (-0.05).toFixed(2))
    }

    @Test
    fun `toFixed rounds rather than truncates`() {
        assertEquals("0.3", 0.25.toFixed(1))
        assertEquals("1.0", 0.96.toFixed(1))
        assertEquals("2.34", 2.344.toFixed(2))
        assertEquals("2.35", 2.346.toFixed(2))
        assertEquals("-0.3", (-0.25).toFixed(1))
    }

    @Test
    fun `csvSafe strips characters that would break a row apart`() {
        // News text is free-form and routinely contains both.
        assertEquals("Knee injury - 50% chance", csvSafe("Knee injury - 50% chance"))
        assertEquals("Ankle injury; back Sep 19", csvSafe("Ankle injury, back Sep 19"))
        assertEquals("Suspended until 19 Sep", csvSafe("Suspended\nuntil 19 Sep"))
    }

    @Test
    fun `player row has one field per header column`() {
        val player = Player(
            id = 367,
            name = "Cody Gakpo",
            team = "Liverpool",
            photoUrl = "",
            points = 28,
            currentPrice = 7.2,
            goalsScored = 3,
            assists = 1,
            elementType = 3,
            teamId = 12,
            status = "d",
            chanceOfPlayingNextRound = 75,
            webName = "Gakpo",
            form = 9.3,
            pointsPerGame = 9.3,
            minutes = 250,
            selectedByPercent = 14.0,
            expectedPointsNext = 7.0,
            news = "Thigh injury, 75% chance of playing",
            bonus = 5,
            cleanSheets = 1,
            goalsConceded = 4,
            saves = 0,
            penaltiesOrder = 3,
            directFreekicksOrder = 2,
            cornersOrder = 4,
            expectedGoalInvolvementsPer90 = 0.56
        )

        val row = playerCsvRow(player, positionName = "MID", teamName = "LIV")

        assertEquals(PLAYER_CSV_HEADER.split(",").size, row.split(",").size)
        assertEquals(
            "367,Gakpo,MID,LIV,7.2,28,9.3,9.3,250,14.0,7.0,0.56,5,1,4,0,3,2,d,Thigh injury; 75% chance of playing",
            row
        )
    }

    @Test
    fun `set piece column takes the best of free kicks and corners`() {
        fun rowWith(freeKicks: Int?, corners: Int?): List<String> = playerCsvRow(
            Player(
                id = 1, name = "A B", team = "T", photoUrl = "", points = 0, currentPrice = 5.0,
                goalsScored = 0, assists = 0, webName = "B",
                directFreekicksOrder = freeKicks, cornersOrder = corners
            ),
            positionName = "MID",
            teamName = "TEA"
        ).split(",")

        val setPieceColumn = PLAYER_CSV_HEADER.split(",").indexOf("sp")
        assertTrue(setPieceColumn > 0)

        assertEquals("1", rowWith(freeKicks = 1, corners = 3)[setPieceColumn])
        assertEquals("2", rowWith(freeKicks = 4, corners = 2)[setPieceColumn])
        assertEquals("3", rowWith(freeKicks = null, corners = 3)[setPieceColumn])
        assertEquals("", rowWith(freeKicks = null, corners = null)[setPieceColumn])
    }
}
