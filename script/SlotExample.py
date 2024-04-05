import json
import yaml

slots = {
    'target': '@xkdeco:roof',
    'slots': [
        {
            'when': {
                'shape': 'straight',
                'facing': 'north'
            },
            'transform_with': 'facing',
            'tag': ['@shape', '@variant', '@half', '@facing'],
            'sides': {
                'north': {
                    'tag': ['*roof:front']
                },
                'west': {
                    'tag': ['*roof:side']
                },
                'east': {
                    'tag': ['*roof:side']
                },
                'up': {
                    'tag': ['*roof:top']
                }
            }
        }
    ]
}

# print(json.dumps(slots, indent='\t'))
print(yaml.dump(slots, indent=2, sort_keys=False))
