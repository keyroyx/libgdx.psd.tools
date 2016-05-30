package com.keyroy.gdx.tools;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellValue;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.json.m.JSONArray;
import org.json.m.JSONObject;

public class XlsxParser {
	private static boolean debug;

	public static final List<JsonPack> parser(File file) throws Exception {
		List<JsonPack> arrays = new ArrayList<JsonPack>();
		if (file.getName().endsWith(".xlsx")) {
			XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file));
			for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
				XSSFSheet sheet = workbook.getSheetAt(i);
				//
				int definingRow = 2;
				XSSFRow row = sheet.getRow(definingRow);
				if (row == null) {
					continue;
				} else {
					HashMap<Integer, ColumnData> cols = new HashMap<Integer, ColumnData>();
					for (int col = 0; col < row.getLastCellNum(); col++) {
						XSSFCell column = row.getCell(col);
						if (column != null && column.getCellType() == XSSFCell.CELL_TYPE_STRING) {
							cols.put(col, new ColumnData(column.getStringCellValue()));
						}
					}

					definingRow++;

					JSONArray array = new JSONArray();
					for (int r = definingRow; r < sheet.getPhysicalNumberOfRows(); r++) {
						row = sheet.getRow(r);
						if (row != null) {
							JSONObject json = new JSONObject();
							for (int col = 0; col < row.getLastCellNum(); col++) {
								ColumnData columnData = cols.get(col);
								XSSFCell column = row.getCell(col);
								if (column != null && columnData != null) {
									if (col == 6) {
										System.out.println("");
									}
									try {
										columnData.format(sheet, column, json);
									} catch (Exception e) {
										e.printStackTrace();
									}
								}
							}
							if (json.length() > 0) {
								array.put(json);
							}
						}
					}
					arrays.add(new JsonPack(sheet.getSheetName(), array));
				}
			}
			workbook.close();
		}
		return arrays;
	}

	// 数据结构
	private static class ColumnData {
		// 变量�?
		String fieldName;
		// 对象类型
		boolean isObject;
		// 数组类型
		boolean isArray;
		// 对象数组类型
		boolean isObjectArray;

		public ColumnData(String columnSource) {
			String[] sp = columnSource.split("#");
			fieldName = sp[0];
			if (sp.length == 2) {
				String symbol = sp[1];
				isObject = symbol.equals("{}");
				isArray = symbol.equals("[]");
				isObjectArray = symbol.equals("[{}]");
			}
		}

		public final void format(XSSFSheet sheet, XSSFCell cell, JSONObject json) {
			String source = getCellData(sheet, cell);
			if (source != null && source.length() > 0) {
				if (isObject) {
					JSONObject object = formatObject(source);
					json.put(fieldName, object);
				} else if (isArray) {
					JSONArray array = new JSONArray();
					String[] sp = source.split(",");
					for (String val : sp) {
						array.put(formatVal(val));
					}
					json.put(fieldName, array);
				} else if (isObjectArray) {
					JSONArray array = new JSONArray();
					String[] sp = source.split(",");
					for (String jsonStr : sp) {//
						JSONObject object = formatObject(jsonStr);
						array.put(object);
					}
					json.put(fieldName, array);
				} else {
					json.put(fieldName, formatVal(source));
				}
			}
		}

		private final String getCellData(XSSFSheet sheet, XSSFCell cell) {
			String source = null;
			if (cell == null) {

			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_NUMERIC) {
				source = "" + cell.getNumericCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_STRING) {
				source = cell.getStringCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BOOLEAN) {
				source = "" + cell.getBooleanCellValue();
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_ERROR) {
				log("Error Cell", cell.getErrorCellString());
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_BLANK) {
				log("Error Blank", cell.getReference());
			} else if (cell.getCellType() == XSSFCell.CELL_TYPE_FORMULA) {

				FormulaEvaluator evaluator = sheet.getWorkbook().getCreationHelper().createFormulaEvaluator();
				CellValue cellValue = evaluator.evaluate(cell);
				if (cellValue.getCellType() == Cell.CELL_TYPE_NUMERIC) {
					source = "" + cellValue.getNumberValue();
				} else if (cellValue.getCellType() == Cell.CELL_TYPE_BOOLEAN) {
					source = "" + cellValue.getBooleanValue();
				} else if (cellValue.getCellType() == Cell.CELL_TYPE_STRING) {
					source = "" + cellValue.getStringValue();
				} else {
					source = cellValue.getStringValue();
				}
			} else {
				log("Error CellType", "" + cell.getCellType());
			}

			return source;
		}

		private final JSONObject formatObject(String source) {
			JSONObject json = new JSONObject();
			String[] sp = source.split(";");
			for (String pair : sp) {
				String[] keyVal = pair.split(":");
				json.put(keyVal[0].trim(), formatVal(keyVal[1]));
			}
			return json;
		}

		private final Object formatVal(String val) {
			val = val.trim();
			try {
				return Integer.parseInt(val);
			} catch (Exception e) {
				try {
					return Float.parseFloat(val);
				} catch (Exception e2) {
				}
			}
			return val;
		}

	}

	private static final void log(String msg) {
		if (debug) {
			System.out.println(msg);
		}
	}

	private static final void log(String key, String msg) {
		log(key + " : " + msg);
	}

}
