import csv
from pathlib import Path

import openpyxl

import Utils
from DataProvider import DataProvider


class TableDataProvider(DataProvider):
    def __init__(self, pack, dataPath: str, table: str, ignoredFields: set = None):
        super().__init__(pack, table, dataPath)
        self.table = table
        self.ignoredFields = ignoredFields if ignoredFields is not None else set()
        self.added = set()

    def generate(self):
        if self.table not in self.pack.config:
            return
        for inputFile in self.pack.config[self.table]:
            ext = Path(inputFile).suffix.lower()
            if ext == '.csv':
                Utils.removeBOM(inputFile)
                with open(inputFile, encoding='utf-8') as csvFile:
                    csvReader = csv.DictReader(csvFile)
                    tableConfig = {}
                    for field in csvReader.fieldnames:
                        if field != 'Name:en_us' and field.startswith('Name:'):
                            tableConfig['SecondaryName'] = field[5:]
                    for row in csvReader:
                        rowDict = {}
                        for key, value in row.items(): #TODO dry-run
                            rowDict[key] = value.strip()
                        self._generateRowPre(rowDict, tableConfig)
            elif ext == '.xlsx':
                # it will return us fewer columns if we use read_only=True, took me one hour to figure out
                workbook = openpyxl.load_workbook(inputFile, read_only=False)
                sheet = workbook[self.table]
                tableConfig = {}
                fields = sheet[1]
                for cell in fields:
                    field = cell.value
                    if field != 'Name:en_us' and field.startswith('Name:'):
                        tableConfig['SecondaryName'] = field[5:]
                for row in sheet.iter_rows(min_row=2, values_only=True):
                    rowDict = {}
                    for i, field in enumerate(fields):
                        value = row[i]
                        rowDict[field.value] = str(value).strip() if value is not None else ''
                    self._generateRowPre(rowDict, tableConfig)
            else:
                raise Exception('Unsupported file format: ' + ext)


    def _generateRowPre(self, row, tableConfig):
        rowId = row['ID']
        if rowId == '':
            return
        if rowId in self.added:
            raise ValueError('Duplicate ID: ' + rowId)
        self.added.add(rowId)
        self.generateRow(row, tableConfig)

    def generateRow(self, row, tableConfig):
        data = {}
        for field in row:
            if field != 'ID' and field not in self.ignoredFields and field != '' and row[field] != '':
                data[field] = row[field]

        self.writeFile(self.pack.defaultResourceLocation(row['ID']), data)

    def __str__(self):
        if self.__class__.__name__ == 'TableDataProvider':
            return 'TableDataProvider: ' + self.table
        return super().__str__()
