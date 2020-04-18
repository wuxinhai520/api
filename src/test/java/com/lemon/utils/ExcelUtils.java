package com.lemon.utils;

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;

import com.lemon.constants.Constants;
import com.lemon.pojo.API;
import com.lemon.pojo.Case;
import com.lemon.pojo.WriteBackData;

import cn.afterturn.easypoi.excel.ExcelImportUtil;
import cn.afterturn.easypoi.excel.entity.ImportParams;

public class ExcelUtils {
	// 所有的API集合
	public static List<API> apiList;
	// 所有的Case集合
	public static List<Case> caseList;
	// excel回写集合
	public static List<WriteBackData> wbdList = new ArrayList<>();

	public static void main(String[] args) throws Exception {

	}

	/**
	 * excel批量回写
	 */
	public static void batchWrite() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		try {
			//1、打开excel（流读excel文件）
			fis = new FileInputStream(Constants.EXCEL_PATH);
			//2、创建excel对象 Workbook
			Workbook workbook = WorkbookFactory.create(fis);
			//遍历wbdList集合
			for(WriteBackData wbd : wbdList) {
				//3、获取对应sheet
				Sheet sheet = workbook.getSheetAt(wbd.getSheetIndex());
				//4、获取对应Row
				Row row = sheet.getRow(wbd.getRowNum());
				//5、获取对应cell
				Cell cell = row.getCell(wbd.getCellNum(), MissingCellPolicy.CREATE_NULL_AS_BLANK);
				cell.setCellType(CellType.STRING);
				//6、setCellValue  设置回写内容
				cell.setCellValue(wbd.getContent());
			}
			//7、回写excel文件
			fos = new FileOutputStream(Constants.EXCEL_PATH);
			workbook.write(fos);
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			// 8、关流
			close(fis);
			close(fos);
		}
	}

	/**
	 * 	关流方法
	 * @param stream 任意流对象
	 */
	private static void close(Closeable stream) {
		try {
			if(stream != null) {
				stream.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 根据apiId获取对应API和List<Case>对象
	 * 
	 * @param apiId 获取API和Case的apiId
	 * @return Object[API][Case]
	 */
	public static Object[][] getAPIAndCaseByApiId(String apiId) {
		// 需要的一个API
		API wantAPI = null;
		// 需要的多个Case集合
		List<Case> wantList = new ArrayList<>();
		// 遍历集合找到符合的API
		for (API api : apiList) {
			// 找到符合要求的API对象(apiId相等)
			if (apiId.equals(api.getId())) {
				wantAPI = api;
				break;
			}
		}
		// 遍历集合找到符合Case
		for (Case c : caseList) {
			// 找到符合要求的Case对象(apiId相等)
			if (apiId.equals(c.getApiId())) {
				wantList.add(c);
			}
		}
		// wantList和wantAPI有值了。
		// API和Case装到Object[apiId对应的Case个数][2个参数]
		Object[][] datas = new Object[wantList.size()][2];
		for (int i = 0; i < datas.length; i++) {
			datas[i][0] = wantAPI;
			datas[i][1] = wantList.get(i);
		}
		return datas;
	}

	/**
	 * 读取excel中的sheet转成对象的List集合
	 * 
	 * @param <T>        实体类型
	 * @param sheetIndex sheet开始索引
	 * @param sheetNum   读取几个sheet
	 * @param clazz      实体类型的字节码对象
	 * @return List<实体类型>的集合
	 */
	public static <T> List<T> read(int sheetIndex, int sheetNum, Class<T> clazz) {
		// 导入验证
		List<T> list = null;
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(Constants.EXCEL_PATH);
			// 导入参数设置类
			ImportParams params = new ImportParams();
			params.setStartSheetIndex(sheetIndex);
			params.setSheetNum(sheetNum);
			list = ExcelImportUtil.importExcel(fis, clazz, params);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(fis);
		}
		return list;
	}

}
