package com.example.artists;

public class verifyStrings {
	
	// Метод для "сборки" строки с количеством альбомов и песен.
	// Подбирает нужную форму слова в зависимости от числа
	public static String verifyTracks (String string, String[] values) {
		if (string.endsWith("1")) {
			if (string.endsWith("11") == false) {
				string = string + values[1];
			}
		} else {
			if (string.endsWith("2") || string.endsWith("3") || string.endsWith("4")) {
				int len = string.length();
				if (len >= 2) {
					len = len - 2;
					if (string.charAt(len) != '1') {
						string = string + values[2];
					} 
				} else {
					string = string + values[2];
				}
			}
		}
		if (string.charAt(string.length()-1) <= '9') {
			string = string + values[0];
		}
		return string;
	}
}
