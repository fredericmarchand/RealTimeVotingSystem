package model;

import java.io.Serializable;

public class Address implements Serializable {
	private static final long serialVersionUID = -4507477610617393544L;

	private String streetNumber;
	private String street;
	private String city;
	private Province province;
	private String postalCode;
	
	public Address() {
		this("0000 unknown street", "Ottawa", Province.Ontario, "A2B8U4");
	}
	
	public Address(String str, String cit, Province prov, String post) {
		street = str;
		city = cit;
		province = prov;
		try {
			setPostalCode(post);
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
