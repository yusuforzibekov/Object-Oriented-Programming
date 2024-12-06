package edu.epam.fop.xml.stax;

public class Food {
	private String id;
	private String name;
	private String price;
	private String description;
	private String calories;

	public Food(String id, String name, String price, String description, String calories) {
		this.id = id;
		this.name = name;
		this.price = price;
		this.description = description;
		this.calories = calories;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getPrice() {
		return price;
	}

	public String getDescription() {
		return description;
	}

	public String getCalories() {
		return calories;
	}

	@Override
	public String toString() {
		return "Food{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", price='" + price + '\'' + ", description='"
				+ description + '\'' + ", calories='" + calories + '\'' + '}';
	}
}