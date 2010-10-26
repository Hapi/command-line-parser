package com.hapiware.util.cmdlineparser.writer;

public interface Writer
{
	public enum Level { NONE, L1, L2, L3, L4, L5 };
	
	public void header();
	public void level1Begin(String text);
	public void level1End();
	public void level2Begin(String text);
	public void level2End();
	public void level3Begin(String text);
	public void level3End();
	public void level4Begin(String text);
	public void level4End();
	public void level5Begin(String text);
	public void level5End();
	public void paragraph(Level headingLevel, String text);
	public void line(Level headingLevel, String text);
	public void listBegin(Level headingLevel);
	public void listItem(String text);
	public void listEnd();
	public void codeBegin(Level headingLevel);
	public void codeLine(String code);
	public void codeEnd();
	public String strongBegin();
	public String strongEnd();
	public void footer();
}
