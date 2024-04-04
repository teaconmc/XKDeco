import yaml

from Pack import Pack


def read(row: dict, pack: Pack) -> dict:
    data = {}
    if 'RenderType' in row and row['RenderType'] != 'solid' and row['RenderType'] != '':
        data['render_type'] = row['RenderType']
    if 'Material' in row and row['Material'] != '':
        materialId = pack.defaultResourceLocation(row['Material'])
        data['material'] = str(materialId)
    if 'LightEmission' in row and row['LightEmission'] != '0' and row['LightEmission'] != 'custom' and row['LightEmission'] != '':
        data['light_emission'] = int(row['LightEmission'])
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
    if 'ColorProvider' in row and row['ColorProvider'] != '':
        data['color_provider'] = row['ColorProvider']
    return data