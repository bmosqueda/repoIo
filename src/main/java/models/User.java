package models;

import org.json.simple.JSONObject;

public class User 
{
	private int user_id;
	private String account_number;
	private String email;
	private String name;
	private int role;
	private int school_id;
	
	public User(String account_number, String name, String email, int role, int school_id) {
		super();
		this.account_number = account_number;
		this.email = email;
		this.role = role;
		this.name = name;
		this.school_id = school_id;
	}
	
	public User(int user_id, String account_number, String name, String email, int role, int school_id) {
		super();
		this.account_number = account_number;
		this.email = email;
		this.name= name;
		this.role = role;
		this.school_id = school_id;
		this.user_id = user_id;
	}
	
	@SuppressWarnings("unchecked")
	public String toJSON() {
		JSONObject json = new JSONObject();
		json.put("email", this.email);
		json.put("name", this.name);
		json.put("role", this.role);
		json.put("school_id", this.school_id);
		json.put("user_id", this.user_id);
		
		return json.toString();
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject() {
		JSONObject json = new JSONObject();
		json.put("email", this.email);
		json.put("name", this.name);
		json.put("role", this.role);
		json.put("school_id", this.school_id);
		json.put("user_id", this.user_id);
		
		return json;
	}
	
	@Override
	public String toString( ) {
		return "Account number: " + this.account_number + "\n"+
				"Email: " + this.email+ "\n"+
				"Name: " + this.name+ "\n"+
				"Role: " + this.role+ "\n"+
				"School_id: " + this.school_id+ "\n"+
				"User_id: " + this.user_id+ "\n";
	}
	
	public String toHTML( ) {
		return "<p>Account number: " + this.account_number + "</p>"+
				"<p>Email: " + this.email+ "</p>"+
				"<p>Name: " + this.name+ "</p>"+
				"<p>Role: " + this.role+ "</p>"+
				"<p>School_id: " + this.school_id+ "</p>"+
				"<p>User_id: " + this.user_id+ "</p><hr>";
	}
	
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public String getAccount_number() {
		return account_number;
	}
	public void setAccount_number(String account_number) {
		this.account_number = account_number;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public int getRole() {
		return role;
	}
	public void setRole(int role) {
		this.role = role;
	}
	public int getSchool_id() {
		return school_id;
	}
	public void setSchool_id(int school_id) {
		this.school_id = school_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}