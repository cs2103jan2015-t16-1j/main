package Testtest;

import java.util.Scanner;
import java.util.regex.Pattern;

public class Testtest {

	private static String _taskName;
	private static StringBuilder _feedback = new StringBuilder();

	public static void main(String args[]) {
		Scanner sc = new Scanner(System.in);
		while (true) {
			String str = sc.nextLine();
			str = extractFindEditName(str);
			System.out.println(_feedback);
			System.out.println(str);
			_feedback = new StringBuilder();
		}

	}

	private static String extractFindEditName(String content) {
		content = content.replaceFirst("\\b(?i)name\\b", "-n");
		int initLength = content.length();
		content = content.replaceFirst("-n", "\\\\");
		int endLength = content.length();
		if(initLength == endLength) {
			return null;
		}
		
		return extractTaskNameWithBackSlash(content);
	}

	private static String extractTaskNameWithBackSlash(String fieldsString) {
		int quoteStart = -1, quoteEnd = -1;
		int i, j;
		for (i = 0; i < fieldsString.length(); i++) {
			if (fieldsString.charAt(i) == 92) {
				quoteStart = i;
				break;
			}
		}

		for (j = fieldsString.length() - 1; j >= 0 && j > i; j--) {
			if (fieldsString.charAt(j) == 92) {
				quoteEnd = j;
				break;
			}
		}

		if (quoteStart != -1 && quoteEnd == -1) {
			_feedback
					.append("Please denote end of task name with the \"\\\" character. ");
			return null;
		} else if (quoteStart != -1 && quoteEnd != -1) {
			_taskName = fieldsString.substring(quoteStart + 1, quoteEnd).trim();
			String front = fieldsString.substring(0, quoteStart).trim();
			String back;
			if(quoteEnd == fieldsString.length() -1) {
				back = "";
			} else {
				back = fieldsString.substring(quoteEnd+1).trim();
			}
			String contentWithoutName = front + " " + back;
			return contentWithoutName;
		} else {
			return null;
		}
	}
}