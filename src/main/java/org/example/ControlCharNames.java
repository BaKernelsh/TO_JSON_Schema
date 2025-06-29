package org.example;

import java.util.Map;

class ControlCharNames {
    private static final Map<Integer, String> CONTROL_CHAR_NAMES = Map.ofEntries(
            Map.entry(0x00, "NUL (Null)"),
            Map.entry(0x01, "SOH (Start of Heading)"),
            Map.entry(0x02, "STX (Start of Text)"),
            Map.entry(0x03, "ETX (End of Text)"),
            Map.entry(0x04, "EOT (End of Transmission)"),
            Map.entry(0x05, "ENQ (Enquiry)"),
            Map.entry(0x06, "ACK (Acknowledge)"),
            Map.entry(0x07, "BEL (Bell)"),
            Map.entry(0x08, "BS (Backspace)"),
            Map.entry(0x09, "TAB (Horizontal Tab)"),
            Map.entry(0x0A, "LF (Line Feed)"),
            Map.entry(0x0B, "VT (Vertical Tab)"),
            Map.entry(0x0C, "FF (Form Feed)"),
            Map.entry(0x0D, "CR (Carriage Return)"),
            Map.entry(0x0E, "SO (Shift Out)"),
            Map.entry(0x0F, "SI (Shift In)"),
            Map.entry(0x10, "DLE (Data Link Escape)"),
            Map.entry(0x11, "DC1 (Device Control 1)"),
            Map.entry(0x12, "DC2 (Device Control 2)"),
            Map.entry(0x13, "DC3 (Device Control 3)"),
            Map.entry(0x14, "DC4 (Device Control 4)"),
            Map.entry(0x15, "NAK (Negative Acknowledge)"),
            Map.entry(0x16, "SYN (Synchronous Idle)"),
            Map.entry(0x17, "ETB (End of Transmission Block)"),
            Map.entry(0x18, "CAN (Cancel)"),
            Map.entry(0x19, "EM (End of Medium)"),
            Map.entry(0x1A, "SUB (Substitute)"),
            Map.entry(0x1B, "ESC (Escape)"),
            Map.entry(0x1C, "FS (File Separator)"),
            Map.entry(0x1D, "GS (Group Separator)"),
            Map.entry(0x1E, "RS (Record Separator)"),
            Map.entry(0x1F, "US (Unit Separator)"),
            Map.entry(0x7F, "DEL (Delete)")
    );

    public static String getControlCharName(char c) {
        return CONTROL_CHAR_NAMES.getOrDefault((int) c, "Not a control character");
    }
}
