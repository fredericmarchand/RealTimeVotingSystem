package model;

public class Address {

	private String streetNumber;
	private String street;
	private String city;
	private Province province;
	private String postalCode;
	
	public Address() {
		streetNumber = "000";
		street = "unknown street";
		city = "Ottawa";
		province = Province.Ontario;
		try {
			setPostalCode("A2B8U4");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public Address(String num, String str, String cit, Province prov, String post) {
		streetNumber = num;
		street = str;
		city = cit;
		province = prov;
		try {
			setPostalCode("post");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public String getStreetNumber() {
		return streetNumber;
	}
	public void setStreetNumber(String streetNumber) {
		this.streetNumber = streetNumber;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Province getProvince() {
		return province;
	}
	public void setProvince(Province province) {
		this.province = province;
	}
	public String getPostalCode() {
		return postalCode;
	}
	public void setPostalCode(String postalCode) throws Exception{
		if (postalCode.length() != 6)
			throw new Exception();
		this.postalCode = postalCode;
	}
}
