import csv
from pathlib import Path
import openpyxl

from script import Utils
from script.DataProvider import DataProvider


class TableDataProvider(DataProvider):
    def __init__(self, pack, dataPath: str, table: str):
        super().__init__(pack, dataPath)
        self.table = table

    def generate(self):
        if self.table not in self.pack.config:
            return
        for inputFile in self.pack.config[self.table]:
            ext = Path(inputFile).suffix
            if ext == '.csv':
                Utils.removeBOM(inputFile)
                with open(inputFile, encoding='utf-8') as csvFile:
                    csvReader = csv.DictReader(csvFile)
                    tableConfig = {}
                    for field in csvReader.fieldnames:
                        if field != 'Name:en_us' and field.startswith('Name:'):
                            tableConfig['SecondaryName'] = field[5:]
                    for row in csvReader:
                        self.generateRow(row, tableConfig)
            elif ext == '.xlsx':
                workbook = openpyxl.load_workbook(inputFile, read_only=True)
                sheet = workbook[self.table]
                tableConfig = {}
                for cell in sheet[1]:
                    field = cell.value
                    if field != 'Name:en_us' and field.startswith('Name:'):
                        tableConfig['SecondaryName'] = field[5:]
                for row in sheet.iter_rows(min_row=2, values_only=True):
                    rowDict = {}
                    for i, field in enumerate(sheet[1]):
                        value = row[i]
                        rowDict[field.value] = str(value) if value is not None else ''
                    self.generateRow(rowDict, tableConfig)
            else:
                raise Exception('Unsupported file format: ' + ext)

    def generateRow(self, row, tableConfig):
        pass
