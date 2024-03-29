import json

from TableDataProvider import TableDataProvider
from Pack import Pack


class BlockDefinitionProvider(TableDataProvider):
    def __init__(self, pack: Pack):
        super().__init__(pack, 'assets/{}/kiwi/block', 'blocks')

    def generateRow(self, row, csvConfig):
        if row['ID'] == '':
            return
        data = {}
        if 'Template' in row and row['Template'] != 'block' and row['Template'] != '':
            index = row['Template'].find('{')
            if index > 0:
                template = {
                    'kiwi:type': row['Template'][:index]
                }
                for key, value in json.loads(row['Template'][index:]).items():
                    template[key] = value
                data['template'] = template
            else:
                data['template'] = row['Template']
        if 'RenderType' in row and row['RenderType'] != 'solid' and row['RenderType'] != '':
            data['render_type'] = row['RenderType']
        if 'Material' in row and row['Material'] != '':
            data['material'] = row['Material']
        if 'LightEmission' in row and row['LightEmission'] != '0' and row['LightEmission'] != 'custom' and row['LightEmission'] != '':
            data['light_emission'] = int(row['LightEmission'])
        if 'SustainsPlant' in row and row['SustainsPlant'].lower() == 'true':
            data['sustains_plant'] = True
        components = []
        if 'WaterLoggable' in row and row['WaterLoggable'].lower() == 'true':
            components.append('water_loggable')
        if 'BaseComponent' in row and row['BaseComponent'] != '':
            components.append(row['BaseComponent'])
        if 'ExtraComponents' in row and row['ExtraComponents'] != '':
            components.extend(json.loads(row['ExtraComponents']))
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
        # item = {}
        # if 'ItemGroup' in row and row['ItemGroup'] != '':
        #     item['tab'] = row['ItemGroup']
        # if len(item) > 0:
        #     data['item'] = item
        if 'ItemGroup' in row and row['ItemGroup'] != '':
            self.pack.creativeTabs.addContent(row['ItemGroup'], self.pack.defaultResourceLocation(row['ID']))
        translationKey = 'block.{namespace}.{name}'.format(namespace=self.pack.config['namespace'], name=row['ID'])
        if 'Name:en_us' in row and row['Name:en_us'] != '':
            self.pack.translations.putTranslation('en_us', translationKey, row['Name:en_us'])
        if 'SecondaryName' in csvConfig:
            fieldName = 'Name:' + csvConfig['SecondaryName']
            if fieldName in row and row[fieldName] != '':
                self.pack.translations.putTranslation(csvConfig['SecondaryName'], translationKey, row[fieldName])

        self.writeJson(self.pack.defaultResourceLocation(row['ID']), data)
