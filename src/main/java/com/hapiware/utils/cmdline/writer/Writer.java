package com.hapiware.utils.cmdline.writer;

public interface Writer
{
	public enum HeadingLevel { H1, H2, H3, H4, H5 };
	
	public void header();
	public void h1(String text);
	public void h2(String text);
	public void h3(String text);
	public void h4(String text);
	public void h5(String text);
	public void paragraph(HeadingLevel headingLevel, String text);
	public void line(HeadingLevel headingLevel, String text);
	public void listBegin(HeadingLevel headingLevel);
	public void listItem(String text);
	public void listEnd();
	public void codeBegin(HeadingLevel headingLevel);
	public void codeLine(String code);
	public void codeEnd();
	public String strongBegin();
	public String strongEnd();
	public void footer();
}
