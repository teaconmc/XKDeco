import yaml

from TableDataProvider import TableDataProvider


def normalize_property_order(data):
    if isinstance(data, list):
        for item in data:
            normalize_property_order(item)
    elif isinstance(data, dict):
        values = data.get('values')
        if data.get('name') == 'half' and isinstance(values, list) and sorted(values) == ['lower', 'upper']:
            data['values'] = ['lower', 'upper']
        for value in data.values():
            normalize_property_order(value)


def read(provider: TableDataProvider, row: dict) -> dict:
    data = {}
    if provider.pack.has('render_type'):
        provider.field(data, 'RenderType', lambda v: v if v != 'solid' else None)
    provider.field(data, 'Material', lambda v: str(provider.pack.defaultIdentifier(v)))
    provider.field(data, 'LightEmission', lambda v: int(float(v)))
    components = []
    if 'WaterLoggable' in row and row['WaterLoggable'].lower() == 'true':
        components.append('water_loggable')
    if 'BaseComponent' in row and row['BaseComponent'] != '':
        components.append(row['BaseComponent'])
    if 'ExtraComponents' in row and row['ExtraComponents'] != '':
        extra_components = yaml.safe_load(row['ExtraComponents'])
        normalize_property_order(extra_components)
        components.extend(extra_components)
    if len(components) > 0:
        data['components'] = components
    provider.field(data, 'Shape', str)
    provider.field(data, 'CollisionShape', str)
    provider.field(data, 'InteractionShape', str)
    provider.field(data, 'NoCollision', lambda v: True if v.lower() == 'true' else None)
    provider.field(data, 'NoOcclusion', lambda v: True if v.lower() == 'true' else None)
    provider.field(data, 'GlassType', str)
    provider.field(data, 'ColorProvider', str)
    provider.field(data, 'PushReaction', str)
    provider.field(data, 'OffsetFunction', str)
    return data
