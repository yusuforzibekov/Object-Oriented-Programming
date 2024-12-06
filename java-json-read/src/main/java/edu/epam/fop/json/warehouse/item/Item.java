package edu.epam.fop.json.warehouse.item;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class Item implements Serializable {

  private long id;
  private String name;
  private LocalDateTime supplyDate;
  private BigInteger price;
  private long totalCount;
  private String barCode;
  private Vendor vendor;
}
