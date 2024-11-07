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


#####interview question

# Specify the required provider
provider "aws" {
  region = "us-west-2"  # Replace with your preferred region
}

# Data block to fetch the AMI ID for "ubuntu22"
data "aws_ami" "ubuntu" {
  most_recent = true
  owners      = ["099720109477"]  # Canonical's account ID for official Ubuntu images
  filter {
    name   = "name"
    values = ["ubuntu/images/hvm-ssd/ubuntu-22.04-amd64-server-*"]
  }
}

# Create 10 EC2 instances
resource "aws_instance" "web_server" {
  count         = 10
  ami           = data.aws_ami.ubuntu.id
  instance_type = "t2.micro"
  key_name      = "my-ec2-key"  # Replace with your key pair name
  security_groups = ["my-security-group"]  # Ensure this security group allows SSH and HTTP

  # User data script for installing Apache and copying index.html
  user_data = <<-EOF
              #!/bin/bash
              sudo apt update
              sudo apt install -y apache2
              echo '${file("index.html")}' | sudo tee /var/www/html/index.html
              sudo systemctl start apache2
              sudo systemctl enable apache2
              EOF

  tags = {
    Name = "WebServer-${count.index + 1}"
  }
}

# Output the public IPs of the instances
output "instance_ips" {
  value = aws_instance.web_server[*].public_ip
}
