package model;

import java.util.ArrayList;

public enum Province {
	BritishColumbia,
	Alberta,
	Saskatchewan,
	Manitoba,
	Ontario,
	Quebec,
	NewBrunswick,
	NovaScotia,
	NewFoundland_Labrador,
	PrinceEdwardIsland,
	Nunavut,
	NorthwestTerritories,
	Yukon;
	
	public static ArrayList<String> getProvinceList() {
			ArrayList<String> list = new ArrayList<String>();
			list.add("British Columbia");
			list.add("Alberta");
			list.add("Saskatchewan");
			list.add("Manitoba");
			list.add("Ontario");
			list.add("Quebec");
			list.add("New Brunswick");
			list.add("Nova Scotia");
			list.add("NewFoundland and Labrador");
			list.add("Prince Edward Island");
			list.add("Nunavut");
			list.add("Northwest Territories");
			list.add("Yukon");
			return list;
		}
		
		public static String getProvinceName(Province p) {
			ArrayList<String> names = getProvinceList();
			return names.get(p.ordinal());
		}
		
		public static void main(String args[]) {
			System.out.println(Province.getProvinceName(Province.BritishColumbia      ));
			System.out.println(Province.getProvinceName(Province.Alberta              ));
			System.out.println(Province.getProvinceName(Province.Saskatchewan         ));
			System.out.println(Province.getProvinceName(Province.Manitoba             ));
			System.out.println(Province.getProvinceName(Province.Ontario              ));
			System.out.println(Province.getProvinceName(Province.Quebec               ));
			System.out.println(Province.getProvinceName(Province.NewBrunswick         ));
			System.out.println(Province.getProvinceName(Province.NovaScotia           ));
			System.out.println(Province.getProvinceName(Province.NewFoundland_Labrador));
			System.out.println(Province.getProvinceName(Province.PrinceEdwardIsland   ));
			System.out.println(Province.getProvinceName(Province.Nunavut              ));
			System.out.println(Province.getProvinceName(Province.NorthwestTerritories ));
			System.out.println(Province.getProvinceName(Province.Yukon                ));
		}
}
