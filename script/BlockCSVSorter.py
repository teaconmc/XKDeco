# Block CSV sorter

import json
import csv
import os
from pathlib import Path

import Utils


def main():
    csvFile = 'xkdeco-blocks.csv'
    jsonFile = 'exported_creative_tabs.json'
    defaultNamespace = 'xkdeco'
    fallbackGroup = 'basic'

    if not Path(csvFile).exists():
        if Path('script/' + csvFile).exists():
            os.chdir('script')
        else:
            print(csvFile + ' not found')
            return
    if not Path(jsonFile).exists():
        print(jsonFile + ' not found')
        return

    Utils.removeBOM(csvFile)
    with open(csvFile, encoding='utf-8') as f:
        csvReader = csv.reader(f)
        header = next(csvReader)
        idIndex = header.index('ID')
        groupIndex = header.index('ItemGroup')
        rowDict = {}
        for row in csvReader:
            rowDict[Utils.defaulted(row[idIndex], defaultNamespace)] = row

    with open(jsonFile, encoding='utf-8') as f:
        jsonFile = json.load(f)

    newRows = []
    for tabId, tabItems in jsonFile.items():
        for item in tabItems:
            if item in rowDict:
                row = rowDict[item]
                row[groupIndex] = Utils.trimRL(tabId, defaultNamespace)
                newRows.append(row)
                rowDict.pop(item)
            else:
                print('Skipping ' + item)

    for item, row in rowDict.items():
        row[groupIndex] = fallbackGroup
        newRows.append(row)

    with open(csvFile, 'w', encoding='utf-8', newline='') as f:
        csvWriter = csv.writer(f)
        csvWriter.writerow(header)
        csvWriter.writerows(newRows)

if __name__ == '__main__':
    main()
