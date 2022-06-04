package net.koko.kaspar.model.dto;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;

@Data
public class KasparItem implements Serializable {
    String id;
    String name;
    int value;
    Timestamp ts;
}
