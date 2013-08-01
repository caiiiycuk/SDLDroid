package com.gamesinjs.dune2.i18n;

public class I18NUtils {
	
	private final static String RUSSIAN_CHARS =
		"абвгдеёжзийклмнопрстуфхцчшщыэюя 1234567890-.,qwertyuiopasdfghjklzxcvbnm/";
	
	private final static String RUSSIAN_CHARS_UPPER = 
		RUSSIAN_CHARS.toUpperCase();
	
	private final static String[] TRANSLIT_CHARS = {"a","b","v","g","d","e","jo","zh","z","i","y","k","l","m","n","o","p",
		"r","s","t","u","f","h","c","ch","sh","sch","y","eh","yu","ya","-","1","2","3","4","5","6","7","8","9","0","-","-","-",
		"q","w","e","r","t","y","u","i","o","p","a","s","d","f","g","h","j","k","l","z","x","c","v","b","n","m","/"};
	
	private final static String[] TRANSLIT_CHARS_UPPER = new String[TRANSLIT_CHARS.length];
	
	static {
		for (int i = 0; i<TRANSLIT_CHARS.length; ++i) {
			TRANSLIT_CHARS_UPPER[i] = TRANSLIT_CHARS[i].toUpperCase();
		}
	}
	
	public static String translitRussian(String text) {
		if (text == null || text.length() == 0) {
			return "";
		}
		
		StringBuffer english = new StringBuffer();
		for (int i=0; i<text.length(); i++) {
			char candiadte = text.charAt(i);
			
			int chrIndex = RUSSIAN_CHARS.indexOf(candiadte);
			int chrUpperIndex = RUSSIAN_CHARS_UPPER.indexOf(candiadte);
			
			if (chrIndex >= 0) {
				english.append(TRANSLIT_CHARS[chrIndex]);
			} else if (chrUpperIndex >= 0) {
				english.append(TRANSLIT_CHARS_UPPER[chrUpperIndex]);
			} else { 
				english.append(candiadte);
			}
		}
		
		return english.toString();
	}
	
}