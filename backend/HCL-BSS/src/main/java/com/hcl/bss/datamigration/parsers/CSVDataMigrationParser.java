package com.hcl.bss.data.migration.parsers;

import static com.hcl.bss.constants.ApplicationConstants.BLANK;
import static com.hcl.bss.constants.ApplicationConstants.COMMA_DELIMITER;
import static com.hcl.bss.constants.ApplicationConstants.EXP_DATE_IDX;
import static com.hcl.bss.constants.ApplicationConstants.PRODUCT_DISPLAY_NAME_IDX;
import static com.hcl.bss.constants.ApplicationConstants.PRODUCT_TYPE_CODE_IDX;
import static com.hcl.bss.constants.ApplicationConstants.PROD_DESCRIPTION_IDX;
import static com.hcl.bss.constants.ApplicationConstants.SKU_IDX;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.hcl.bss.dto.ProductDto;
import com.hcl.bss.exceptions.ImportParseException;
import com.hcl.bss.validator.DataMigrationFieldValidator;

@Component
public class CSVDataMigrationParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(CSVDataMigrationParser.class);
	@Autowired
	DataMigrationFieldValidator dataMigrationFieldValidator;
	@Value("${upload.file.header}")
	String prescribedFileHeader;

	public List<ProductDto> parseCsvData(String fileName) throws IOException, ParseException {
		List<ProductDto> listProduct = new ArrayList<>();
		BufferedReader fileReader = null;

		try {

			String line = BLANK;

			// Create the file reader
			fileReader = new BufferedReader(new FileReader(fileName));

			// Read the CSV file header
			String header = getCSVHeader(fileReader);

			if (prescribedFileHeader.equalsIgnoreCase(header)) {

				// Read the file line by line starting from the second line
				while ((line = fileReader.readLine()) != null) {

					// Get all tokens available in line
					String[] tokens = line.split(COMMA_DELIMITER, -1);
					if (0 != tokens.length) {
						if (tokens.length == header.split(COMMA_DELIMITER).length) {
							// Create a new product object and fill this data
							// TODO - Use Builder Pattern. ProductDto.......build()
							ProductDto product = new ProductDto(tokens[PRODUCT_TYPE_CODE_IDX],
									tokens[PRODUCT_DISPLAY_NAME_IDX], tokens[SKU_IDX], tokens[EXP_DATE_IDX],
									tokens[PROD_DESCRIPTION_IDX]);

							listProduct.add(product);

						}
					} else {
						throw new ImportParseException();
					}
					// TODO - Remove after UT. Print the new product list
					for (ProductDto prod : listProduct) {
						LOGGER.info(prod.toString());

					}

				}
			} else {
				throw new ImportParseException();
			}
		}

		finally {
			try {
				fileReader.close();
			} catch (IOException e) {
				e.printStackTrace();

			}
		}
		return listProduct;

	}

	private String getCSVHeader(BufferedReader fileReader) {
		String header = BLANK;
		try {
			header = fileReader.readLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return header;
	}

}