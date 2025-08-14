##Backup Script
#!/bin/bash
source_dir="/path/to/source"
backup_dir="/path/to/backup"
date=$(date +%Y-%m-%d)

mkdir -p $backup_dir/$date
cp -r $source_dir/* $backup_dir/$date/

echo "Backup completed successfully!"
-------------------------------------------------------------------------------------------------------------------
## DISK memory reachs high
#!/bin/bash
threshold=80
usage=$(df / | grep / | awk '{ print $5 }' | sed 's/%//g')

if [ $usage -gt $threshold ]; then
  echo "Warning: Disk usage is above $threshold%!"
else
  echo "Disk usage is below $threshold%."
fi
-----------------------------------------------------------------------------------------------------------------------
#!/bin/bash

# Apache service name may vary (apache2 on Debian/Ubuntu, httpd on CentOS/RHEL)
SERVICE="apache2"  # change to "httpd" if using CentOS/RHEL

# Check if Apache is active
if systemctl is-active --quiet "$SERVICE"; then
    echo "✅ Apache ($SERVICE) is running."
else
    echo "⚠️ Apache ($SERVICE) is not running. Starting..."
    sudo systemctl start "$SERVICE"

    # Verify if it started successfully
    if systemctl is-active --quiet "$SERVICE"; then
        echo "✅ Apache ($SERVICE) started successfully."
    else
        echo "❌ Failed to start Apache ($SERVICE). Check logs."
    fi
fi
