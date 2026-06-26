import titlecase
import yaml

import ItemPropertiesReader
from Pack import Pack
from Identifier import Identifier
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

    def generateRow(self, row, tableConfig):
        self.order.append(row['ID'])
        itemId = self.pack.defaultIdentifier(row['ID'])
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
            templateId = Identifier(templateId)
            templateString = str(templateId)
            if templateString == 'minecraft:none':
                self.writeFile(itemId, data)
                self.pack.providers['creative_tabs'].removeContent(itemId)
                return
            if itemId in self.pack.providers['blocks'].blocks:
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
            self.pack.providers['block_families'].addItem(self.pack.defaultIdentifier(row['MainFamily']), itemId)

        if hasTranslation:
            hasTranslation = 'Name:en_us' not in row or row['Name:en_us'].lower() != 'n/a'
        if hasTranslation:
            translationKey = 'item.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
            self.processRowTranslations(row, translationKey)
            if 'Name:en_us' not in row or row['Name:en_us'] == '':
                self.pack.providers['translations'].putTranslation('en_us', translationKey,
                                                                   titlecase.titlecase(itemId.path.replace('_', ' ')))

        for tag in tags:
            self.pack.providers['item_tags'].addBlock(tag, itemId)

        self.writeFile(itemId, data)
