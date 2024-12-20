import boto3
def get_volume_id_from_arn(volume_arn):
  #split the ARN using colon (':') seperator
  arn_parts = volumne_arn.split(':')
  # The volume id is last part of ARN after the '/volume' prefix
  volume_id = arn_parts[-1].split('/)[-1]
  return volume_id
def lamda_handler(event, context):
  volume_arn = event['resource'][0]
  volume_id = get_volume_id_from_arn(volume_arn)
ec2_client = boto3.client('ec2')

response = ec2_client.modify_volume(
    VolumeId=volume_id,
    VolumeType='gp3',
   )
