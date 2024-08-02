### main.tf
provider "aws" {
  region = var.aws_region
}

resource "aws_instance" "example_terraform_test" {
  ami           = var.ami_id
  instance_type = var.instance_type
  subnet_id     = var.subnet_id
  user_data     = base64encode(file("userdata.sh"))
  key_name = var.key_name

  vpc_security_group_ids = [var.security_group_id]

  tags = {
    Name = "ExampleInstance"
  }
}

### variable.tf
variable "aws_region" {
  description = "The AWS region to deploy to"
}

variable "vpc_id" {
  description = "The ID of the VPC"
}

variable "subnet_id" {
  description = "The ID of the subnet within the VPC"
}

variable "security_group_id" {
  description = "The ID of the security group"
}

variable "instance_type" {
  description = "The type of the EC2 instance"
}

variable "ami_id" {
  description = "The ID of the AMI to use for the instance"
}

variable "key_name" {
  description = "The name of the existing key pair"
  type        = string
}

###terraform.tfvars
aws_region        = "us-east-1"
vpc_id            = "vpc-00b8107e27afdfd2b"
subnet_id         = "subnet-0453fc456d75b7847"
security_group_id = "sg-0cba6778782064335"
ami_id            = "ami-0a0e5d9c7acc336f1"
instance_type     = "t2.micro"
key_name          = "new_key"
