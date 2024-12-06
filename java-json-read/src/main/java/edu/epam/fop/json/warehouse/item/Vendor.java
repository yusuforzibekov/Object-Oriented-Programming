package edu.epam.fop.json.warehouse.item;

import java.io.Serializable;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Vendor implements Serializable {
  private long id;
  private String name;
}
