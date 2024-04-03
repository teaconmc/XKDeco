import yaml

from Pack import Pack
from ResourceLocation import ResourceLocation
from TableDataProvider import TableDataProvider


class BlockDefinitionProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/block', 'blocks')
        self.templateTags = None
        self.tagTransformers = None

    def generate(self):
        self.templateTags = self.pack.providers['block_templates'].tags
        self.tagTransformers = self.pack.providers['materials'].tagTransformers
        # for key, value in self.templateTags.items():
        #     print(str(key), value)
        super().generate()

    def generateRow(self, row, csvConfig):
        if row['ID'] == '':
            return
        blockId = self.pack.defaultResourceLocation(row['ID'])
        data = {}
        tags = set()
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
            if templateId in self.templateTags:
                tags.update(self.templateTags[templateId])
        if 'RenderType' in row and row['RenderType'] != 'solid' and row['RenderType'] != '':
            data['render_type'] = row['RenderType']
        if 'Material' in row and row['Material'] != '':
            materialId = self.pack.defaultResourceLocation(row['Material'])
            data['material'] = str(materialId)
            if materialId in self.tagTransformers:
                for key, value in self.tagTransformers[materialId].items():
                    if key == '':
                        tags.update(value)
                        continue
                    key = ResourceLocation(key)
                    if key in tags:
                        tags.remove(key)
                        tags.update(value)
        if 'LightEmission' in row and row['LightEmission'] != '0' and row['LightEmission'] != 'custom' and row['LightEmission'] != '':
            data['light_emission'] = int(row['LightEmission'])
        if 'SustainsPlant' in row and row['SustainsPlant'].lower() == 'true':
            self.pack.providers['block_tags'].addEntry(ResourceLocation('kiwi:sustains_plant'), blockId)
        components = []
        if 'WaterLoggable' in row and row['WaterLoggable'].lower() == 'true':
            components.append('water_loggable')
        if 'BaseComponent' in row and row['BaseComponent'] != '':
            components.append(row['BaseComponent'])
        if 'ExtraComponents' in row and row['ExtraComponents'] != '':
            components.extend(yaml.safe_load(row['ExtraComponents']))
        if len(components) > 0:
            data['components'] = components
        if 'Shape' in row and row['Shape'] != '':
            data['shape'] = row['Shape']
        if 'CollisionShape' in row and row['CollisionShape'] != '':
            data['collision_shape'] = row['CollisionShape']
        if 'InteractionShape' in row and row['InteractionShape'] != '':
            data['interaction_shape'] = row['InteractionShape']
        if 'NoCollision' in row and row['NoCollision'].lower() == 'true':
            data['no_collision'] = True
        if 'NoOcclusion' in row and row['NoOcclusion'].lower() == 'true':
            data['no_occlusion'] = True
        if 'GlassType' in row and row['GlassType'] != '':
            data['glass_type'] = row['GlassType']
            if row['GlassType'] != 'hollow_steel':
                self.pack.providers['block_tags'].addEntry(ResourceLocation('impermeable'), blockId)
        if 'ColorProvider' in row and row['ColorProvider'] != '':
            data['color_provider'] = row['ColorProvider']
        # item = {}
        # if 'ItemGroup' in row and row['ItemGroup'] != '':
        #     item['tab'] = row['ItemGroup']
        # if len(item) > 0:
        #     data['item'] = item
        if 'ItemGroup' in row and row['ItemGroup'] != '':
            self.pack.providers['creative_tabs'].addContent(row['ItemGroup'], blockId)
        translationKey = 'block.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
        if 'Name:en_us' in row and row['Name:en_us'] != '':
            self.pack.providers['translations'].putTranslation('en_us', translationKey, row['Name:en_us'])
        if 'SecondaryName' in csvConfig:
            fieldName = 'Name:' + csvConfig['SecondaryName']
            if fieldName in row and row[fieldName] != '':
                self.pack.providers['translations'].putTranslation(csvConfig['SecondaryName'], translationKey, row[fieldName])

        for tag in tags:
            self.pack.providers['block_tags'].addEntry(tag, blockId)

        self.writeJson(blockId, data)
