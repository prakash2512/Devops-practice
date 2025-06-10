import React from 'react';
import dynamic from 'next/dynamic';
import styles from './card.module.css'; // Import the CSS module

// Dynamically import ReactApexChart to disable SSR (Server Side Rendering)
const ReactApexChartNoSSR = dynamic(() => import('react-apexcharts'), { ssr: false });

const BarChartCard = ({ data }) => {
    const { title, values, categories, width, height, barColors } = data;

    // Default colors if barColors is not passed in as a prop
    const defaultColors = ['#00bcd4', '#FF5733', '#8E44AD', '#1ABC9C', '#F39C12', '#E74C3C'];

    // Ensure we use the passed colors or fallback to default colors
    const colors = barColors || defaultColors;

    const options = {
        series: [{
            data: values || []
        }],
        chart: {
            type: 'bar',
            height: '100%' // Keep chart height responsive
        },
        plotOptions: {
            bar: {
                borderRadius: 4,
                borderRadiusApplication: 'end',
                horizontal: true, // Keeps bars horizontal
            }
        },
        dataLabels: {
            enabled: true, // Enable data labels
            offsetX: 10,   // Adjust the position of the label horizontally
            style: {
                colors: ['white'], // Set the text color for data labels
                fontSize: '12px',  // Adjust font size
            },
            formatter: function (val) {
                return `${val}`; // Format the value if needed (can add text or units)
            }
        },
        xaxis: {
            categories: categories || [],
            labels: {
                show: false, // Hide the x-axis labels (values)
            },
            axisBorder: {
                show: false, // Hide the axis border line
            },
            axisTicks: {
                show: false, // Hide the axis ticks
            }
        },
        yaxis: {
            labels: {
                style: {
                    colors: ['#00FFFF'], // Set y-axis label color to #00bcd4
                    fontSize: '14px',
                }
            }
        },
        toolbar: {
            show: false, // Keep toolbar visible
            tools: {
                download: false, // Disable the download button
                zoom: false,     // Optionally disable zoom button
                zoomin: false,   // Optionally disable zoom-in button
                zoomout: false,  // Optionally disable zoom-out button
                pan: false,      // Optionally disable pan button
                reset: false     // Optionally disable reset button
            }
        },
        colors: colors // Apply different colors to each bar based on the categories
    };

    return (
        <div className={styles.card}
            style={{
                maxWidth: `${width}px`,
            }}
        >
            <h3 className={styles.title}>{title}</h3>
            <div className={styles.chartContainer}
                style={{
                    height: `${height}px`
                }}
            >
                {/* Use dynamically imported ReactApexChart */}
                <ReactApexChartNoSSR options={options} series={options.series} type="bar" height="100%" />
            </div>
        </div>
    );
};


export default BarChartCard;
