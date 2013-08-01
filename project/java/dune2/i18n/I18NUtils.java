package com.gamesinjs.dune2.i18n;

public class I18NUtils {
	
	public final static String RUSSIAN_CHARS_URL = "абвгдеёжзийклмнопрстуфхцчшщыэюя 1234567890-.,qwertyuiopasdfghjklzxcvbnm/";
	public final static String[] TRANSLIT_URL_CHARS = {"a","b","v","g","d","e","jo","zh","z","i","y","k","l","m","n","o","p",
		"r","s","t","u","f","h","c","ch","sh","sch","y","eh","yu","ya","-","1","2","3","4","5","6","7","8","9","0","-","-","-",
		"q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m","/"}; 
	
	public static String translitRussian(String russian) {
		if (russian == null || russian.length() == 0) {
			return "";
		}
		
		russian = russian.toLowerCase();
		
		StringBuffer englishUrl = new StringBuffer();
		for (int i=0; i<russian.length(); i++) {
			char candiadte = russian.charAt(i);
			
			int chrIndex = RUSSIAN_CHARS_URL.indexOf(candiadte);
			if (chrIndex == -1) {
				englishUrl.append(candiadte);
			} else { 
				englishUrl.append(TRANSLIT_URL_CHARS[chrIndex]);
			}
		}
		return englishUrl.toString();
	}
	
}