package net.koko.kaspar.model.data;

import lombok.Data;

import java.io.Serializable;

@Data
public class KasparItem implements Serializable {
    String key;
    String msg;
}
