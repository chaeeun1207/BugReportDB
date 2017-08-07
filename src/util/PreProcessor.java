package util;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.lang3.StringUtils;

public class PreProcessor {
	static Inflector inf = new Inflector();;
	static ContractionsExpansor cont = new ContractionsExpansor();
	public static String newStopRemoval(String content) {
		ArrayList<String> resultKeywords = new ArrayList<String>();
		String resultKey = "observed expected error missing crash freeze boot exception fail wrong correct"; 
		String[] resultKeys = PreProcessor.stemContentNatural(Splitter.splitNatureLanguageEx(resultKey)).split(" ");
		Collections.addAll(resultKeywords, resultKeys);
		
		String result ="";
		for(int i = 0 ; i<resultKeywords.size(); i++){
			if(content.contains(resultKeywords.get(i)))
				result = content.replace(resultKeywords.get(i), " ");
		}
		if(result.equals(""))
			return content;
		else
			return result;
	}
	public static String[] splitNatureLanguageEx(String natureLanguage) {
		if(inf == null)
			inf = Inflector.getInstance();
		ArrayList<String> wordList = new ArrayList<String>();
		StringBuffer wordBuffer = new StringBuffer();
		
		char ac[] = natureLanguage.toCharArray();
		for (int l = 0; l < natureLanguage.toCharArray().length; l++) {
			char c = ac[l];
			if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '\'') {
				wordBuffer.append(c);
			} else {
				String word = wordBuffer.toString();
				if(word.length() < 3) continue;							
				wordList.add(inf.singularize(word));
				wordList.add(word);	// add full identifier

				if (word.lastIndexOf("\'s") > 0) {
					wordList.add(word.substring(0, word.lastIndexOf("\'s")));
				} else if (!word.contains("\'")) { 	// except as like "I'll", "I've"		
					String[] splitWords = StringUtils.splitByCharacterTypeCamelCase(word);
					if (splitWords.length > 1) {
						for (int i = 0; i < splitWords.length; i++) {
							if(splitWords[i].length() < 3) continue;							
							wordList.add(inf.singularize(splitWords[i]));
							
						}
					}
				}
				wordBuffer = new StringBuffer();
			}
		}

		if (wordBuffer.length() != 0) {
			String word = wordBuffer.toString();
			if (!word.equals("") && !word.equals(" "))
				wordList.add(word);
			wordBuffer = new StringBuffer();
		}
		return (String[]) wordList.toArray(new String[wordList.size()]);
	}
	
	public static String[] splitNatureLanguage(String natureLanguage) {
		ArrayList<String> wordList = new ArrayList<String>();
		StringBuffer wordBuffer = new StringBuffer();
		char ac[] = natureLanguage.toCharArray();
		for (int i = 0; i < natureLanguage.toCharArray().length; i++) {
			char c = ac[i];
			if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z' || c >= '0' && c <= '9' || c == '\'') {
				wordBuffer.append(c);
			} else {
				String word = wordBuffer.toString();
				if (!word.equals("") && word.length() > 2)
					wordList.add(word);
				wordBuffer = new StringBuffer();
			}
		}
	
		if (wordBuffer.length() != 0) {
			String word = wordBuffer.toString();
			if (!word.equals(""))
				wordList.add(word);
			wordBuffer = new StringBuffer();
		}
		return (String[]) wordList.toArray(new String[wordList.size()]);
		
	}
		
	
	public static String stemContentNatural(String content[]) {
		StringBuffer contentBuf = new StringBuffer();
		for (int i = 0; i < content.length; i++) {
			String word = content[i].toLowerCase();
			if (word.length() > 0) {
				String stemWord = Stem.stem(word);
				
				// debug code
//					System.out.printf("%d stemWord: %s\n", i, stemWord);
//					if (stemWord.contains("keys")) {
//						System.out.println("stemWord: " + stemWord);
//					}
				
				// Do NOT user Stopword.isKeyword() for BugCorpusCreator.
				// Because bug report is not source code.				
				try{
					if (!Stopword.isEnglishStopword(stemWord) && !Stopword.isProjectKeyword(stemWord) 
							&& !Stopword.isJavaKeyword(stemWord)) {
						int num = Integer.parseInt(stemWord);
					}
				}catch(Exception e){
					contentBuf.append(stemWord);
					contentBuf.append(" ");
				}
			}
		}
		return contentBuf.toString();
	}
	
	public static String[] splitSourceCode(String sourceCode) {
		StringBuffer contentBuf = new StringBuffer();
		StringBuffer wordBuf = new StringBuffer();
		sourceCode = (new StringBuilder(String.valueOf(sourceCode))).append("$").toString();
		char ac[] = sourceCode.toCharArray();
		for (int l = 0; l < sourceCode.toCharArray().length; l++) {
			char c = ac[l];
			if (c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z') {
				wordBuf.append(c); // append a character to wordBuf
			} else {
				int length = wordBuf.length();
				if (length != 0) {
					int k = 0;
					int i = 0;
					
					// split words written in CamelCase style
					for (int j = 1; i < length - 1; j++) {
						char first = wordBuf.charAt(i);
						char second = wordBuf.charAt(j);
						if (first >= 'A' && first <= 'Z' && second >= 'a' && second <= 'z') {
							contentBuf.append(wordBuf.substring(k, i));
							contentBuf.append(' ');
							k = i;
						} else if (first >= 'a' && first <= 'z' && second >= 'A' && second <= 'Z') {
							contentBuf.append(wordBuf.substring(k, j));
							contentBuf.append(' ');
							k = j;
						}
						i++;
					}

					if (k < length) {
						contentBuf.append(wordBuf.substring(k));
						contentBuf.append(" ");
					}
					wordBuf = new StringBuffer();
				}
			}
		}

		String words[] = contentBuf.toString().split(" ");
		contentBuf = new StringBuffer();
		for (int i = 0; i < words.length; i++)
			if (!words[i].trim().equals("") && words[i].length() >= 2)
				contentBuf.append((new StringBuilder(String.valueOf(words[i])))
						.append(" ").toString());

		return contentBuf.toString().trim().split(" ");
	}
	
	public static String stemContentSource(String contents[]) {
		StringBuffer contentBuf = new StringBuffer();
		for (int i = 0; i < contents.length; i++) {
			String word = contents[i].toLowerCase();
			if (word.length() > 0) {
				String stemWord = Stem.stem(word);
				if (!Stopword.isJavaKeyword(stemWord) && !Stopword.isProjectKeyword(stemWord) && !Stopword.isEnglishStopword(stemWord)) {
					contentBuf.append(stemWord);
					contentBuf.append(" ");
				}
			}
		}
		return contentBuf.toString();
	}
}
