package com.horsehour.filter;

/**
 * �����˽��������ݵĲ��
 * @author Chunheng Jiang
 * @version 1.0
 * @since 20130327
 */
public interface LineParserFilter {
	public Object parse(String line);
	public String name();
}