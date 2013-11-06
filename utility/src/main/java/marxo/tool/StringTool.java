package marxo.tool;

import java.util.concurrent.ThreadLocalRandom;

public class StringTool {
	public final static String regexEscapeString = "([\\$\\^\\[\\]\\{\\}\\(\\)\\.\\|\\+\\*\\?\\\\])";
	static double[] alphabetsFrequencies = {8.167, 9.659, 12.441, 16.694, 29.396, 31.624, 33.639, 39.733, 46.699, 46.852, 47.624, 51.649, 54.055, 60.804, 68.311, 70.24, 70.335, 76.322, 82.649, 91.705, 94.463, 95.441, 97.801, 97.951, 99.925};
	static ThreadLocalRandom random = ThreadLocalRandom.current();

	private StringTool() {
	}

	public static String escapePatternCharacters(String patternString) {
		return patternString.replaceAll(regexEscapeString, "\\\\$1");
	}

	public static String getRandomString(int numOfChars) {
		StringBuilder stringBuilder = new StringBuilder();
		boolean isSpace = true;
		boolean isDot = false;
		boolean isInSentence = false;
		boolean isComma = false;

		for (int i = 0; i < numOfChars; i++) {
			if (i >= numOfChars - 1) {
				stringBuilder.append('.');
				isDot = true;
				continue;
			}

			if (isSpace) {
				char temp = getRandomCharAccordingToFrequency();

				if (isInSentence) {
					stringBuilder.append(temp);
				} else {
					stringBuilder.append(Character.toUpperCase(temp));
					isInSentence = true;
				}

				isSpace = false;
				continue;
			}

			if (isDot) {
				stringBuilder.append(' ');
				isSpace = true;
				isDot = false;
				continue;
			}

			if (isComma) {
				stringBuilder.append(' ');
				isSpace = true;
				isComma = false;
				continue;
			}

			if (i >= numOfChars - 2) {
				isSpace = false;
			} else {
				isSpace = random.nextDouble(100) <= 17.1662;
			}

			if (isSpace) {
				stringBuilder.append(' ');
				continue;
			}

			if (i >= numOfChars - 3) {
				isDot = false;
			} else {
				isDot = random.nextDouble(100) <= 1.5124;
			}

			if (isDot) {
				stringBuilder.append('.');
				isInSentence = false;
				isDot = true;
				continue;
			}

			if (i >= numOfChars - 3) {
				isComma = false;
			} else {
				isComma = random.nextDouble(100) <= 0.7384;
			}

			if (isComma) {
				stringBuilder.append(',');
				continue;
			}

			stringBuilder.append(getRandomCharAccordingToFrequency());
		}

		return stringBuilder.toString();
	}

	public static char getRandomCharAccordingToFrequency() {
		double dice = random.nextDouble(100);

		for (int i = 0; i < alphabetsFrequencies.length; i++) {
			if (dice < alphabetsFrequencies[i]) {
				return (char) ('a' + i);
			}
		}

		return 'z';
	}
}
