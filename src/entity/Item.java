package entity;

import  java.util.Set;

import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;


public class Item {
	

	private String  itemId;
	private String name;
	private double rating;
	private String address;
	private Set<String> categories;
	private String imageUrl;
	private String url;
	private double distance;

	

	private Item(ItemBuilder builder) {
		this.itemId = builder.itemId;
		this.name = builder.name;
		this.rating = builder.rating;
		this.address = builder.address;
		this.categories = builder.categories;
		this.imageUrl = builder.imageUrl;
		this.distance = builder.distance;
	}

	public String getItemId() {
		return itemId;
	}

	public void setItemId(String itemId) {
		this.itemId = itemId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getRating() {
		return rating;
	}

	public void setRating(double rating) {
		this.rating = rating;
	}

	public String getAddress() {
		return address;
	}

	public void setAdderss(String adderss) {
		this.address = adderss;
	}

	public Set<String> getCategories() {
		return categories;
	}

	public void setCategories(Set<String> categories) {
		this.categories = categories;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	//前段读不懂java class，只懂JSON
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();
		try {
			obj.put("item_id",itemId);
			obj.put("name",name);
			obj.put("rating",address);
			obj.put("categiries",new JSONArray(categories));
			obj.put("image_url",imageUrl);
			obj.put("distance",distance);
		
			
		}catch(JSONException e) {
			e.printStackTrace();
		}
		return obj;
	}
	

	
	public static class ItemBuilder{
		private String  itemId;
		private String name;
		private double rating;
		private String address;
		private Set<String> categories;
		private String imageUrl;
		private String url;
		private double distance;
		
		
		
		public void setItemId(String itemId) {
			this.itemId = itemId;
		}
		
		public void setName(String name) {
			this.name = name;
		}
		
		public void setRating(double rating) {
			this.rating = rating;
		}
		public void setAddress(String adderss) {
			this.address = adderss;
		}
		
		public void setCategories(Set<String> categories) {
			this.categories = categories;
		}
		
		public void setImageUrl(String imageUrl) {
			this.imageUrl = imageUrl;
		}
		
		public void setUrl(String url) {
			this.url = url;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		public Item build() {
			return new Item(this);
		}




		
	}








}
