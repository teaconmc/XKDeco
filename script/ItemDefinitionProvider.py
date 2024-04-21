import yaml

import BlockPropertiesReader
import ItemPropertiesReader
from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class ItemDefinitionProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/item', 'items')
        self.templateTags = None
        self.templateProperties = None
        self.order = []

    def generate(self):
        self.templateTags = self.pack.providers['item_templates'].tags
        self.templateProperties = self.pack.providers['item_templates'].properties
        super().generate()
        self.pack.providers['metadata'].putRegistryOrder('item', self.order)

    def generateRow(self, row, csvConfig):
        self.order.append(row['ID'])
        itemId = self.pack.defaultResourceLocation(row['ID'])
        data = {}
        tags = set()
        templateId = None
        hasTranslation = True
        if 'Template' in row and row['Template'] != 'block' and row['Template'] != '':
            index = row['Template'].find('{')
            if index > 0:
                templateId = row['Template'][:index]
                template = {
                    'kiwi:type': templateId
                }
                for key, value in yaml.safe_load(row['Template'][index:]).items():
                    template[key] = value
                data['template'] = template
            else:
                templateId = row['Template']
                data['template'] = templateId
            templateId = ResourceLocation(templateId)
            templateString = str(templateId)
            if templateString == 'minecraft:none':
                self.writeFile(itemId, data)
                self.pack.providers['creative_tabs'].removeContent(itemId)
                return
            if templateString == 'minecraft:block':
                hasTranslation = False
            if templateId in self.templateTags:
                tags.update(self.templateTags[templateId])

        properties = ItemPropertiesReader.read(row, self.pack)
        data.update(properties)
        if templateId is not None and templateId in self.templateProperties:
            properties.update(self.templateProperties[templateId])

        if 'ItemGroup' in row and row['ItemGroup'] != '':
            self.pack.providers['creative_tabs'].addContent(row['ItemGroup'], itemId)
        if 'MainFamily' in row and row['MainFamily'] != '':
            self.pack.providers['block_families'].addItem(self.pack.defaultResourceLocation(row['MainFamily']), itemId)

        if hasTranslation:
            self._addTranslation(row, csvConfig, itemId)

        for tag in tags:
            self.pack.providers['item_tags'].addBlock(tag, itemId)

        self.writeFile(itemId, data)

    def _addTranslation(self, row, csvConfig, itemId):
        translationKey = 'item.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
        if 'Name:en_us' in row and row['Name:en_us'] != '':
            if row['Name:en_us'].lower() == 'n/a':
                return
            self.pack.providers['translations'].putTranslation('en_us', translationKey, row['Name:en_us'])
        else:
            parts = itemId.path.split('_')
            translatedName = ' '.join(parts).title()
            self.pack.providers['translations'].putTranslation('en_us', translationKey, translatedName)
        if 'SecondaryName' in csvConfig:
            fieldName = 'Name:' + csvConfig['SecondaryName']
            if fieldName in row and row[fieldName] != '':
                self.pack.providers['translations'].putTranslation(csvConfig['SecondaryName'], translationKey, row[fieldName])
