import yaml

from TableDataProvider import TableDataProvider


def read(provider: TableDataProvider, row: dict) -> dict:
    data = {}
    provider.field(data, 'RenderType', lambda v: v if v != 'solid' else None)
    provider.field(data, 'Material', lambda v: str(provider.pack.defaultResourceLocation(v)))
    provider.field(data, 'LightEmission', lambda v: int(float(v)))
    components = []
    if 'WaterLoggable' in row and row['WaterLoggable'].lower() == 'true':
        components.append('water_loggable')
    if 'BaseComponent' in row and row['BaseComponent'] != '':
        components.append(row['BaseComponent'])
    if 'ExtraComponents' in row and row['ExtraComponents'] != '':
        components.extend(yaml.safe_load(row['ExtraComponents']))
    if len(components) > 0:
        data['components'] = components
    provider.field(data, 'Shape', str)
    provider.field(data, 'CollisionShape', str)
    provider.field(data, 'InteractionShape', str)
    provider.field(data, 'NoCollision', lambda v: True if v.lower() == 'true' else None)
    provider.field(data, 'NoOcclusion', lambda v: True if v.lower() == 'true' else None)
    provider.field(data, 'GlassType', str)
    provider.field(data, 'ColorProvider', str)
    return data
