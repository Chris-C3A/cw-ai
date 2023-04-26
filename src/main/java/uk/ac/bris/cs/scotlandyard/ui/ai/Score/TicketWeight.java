package uk.ac.bris.cs.scotlandyard.ui.ai.Score;

/**
 * Enum for ticket weights
 */
public enum TicketWeight {
    SECRET_HEAVY(8),
    TAXI(4),
    BUS(3),
    UNDERGROUND(2),
    SECRET_LIGHT(1);

    private final int value;

    TicketWeight(final int newValue) {
            value = newValue;
    }

    public int getValue() { return value; }

}
