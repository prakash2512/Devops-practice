import React from 'react';
import dynamic from 'next/dynamic';
import styles from './pi.module.css';

// Dynamically import ReactApexChart to disable SSR (Server Side Rendering)
const ReactApexChartNoSSR = dynamic(() => import('react-apexcharts'), { ssr: false });

function PieChart({ data, labels, colors }) {
    // Default data values if not passed
    const values = data || [44, 55, 13, 33]; // Default values as per your second example
    const categories = labels || ['Category A', 'Category B', 'Category C', 'Category D']; // Default labels
    const pieColors = colors || ['#FF5733', '#1ABC9C', '#8E44AD', '#F39C12']; // Default colors

    const options = {
        series: values, // Dynamic values passed via props
        chart: {
            type: 'donut', // Change this to 'donut' to make it a donut chart
            width: 500, // Increased width (You can set to any value)
        },
        labels: categories, // Dynamic labels
        colors: pieColors, // Dynamic colors
        dataLabels: {
            enabled: false, // Disable data labels on the outer chart
        },
        responsive: [{
            breakpoint: 380,
            options: {
                chart: {
                    width: 400, // For small screens, you can reduce the width
                },
                legend: {
                    show: false, // Hide legend on small screens
                },
            },
        }],
        legend: {
            position: 'bottom', // Position of the legend
            offsetY: 0,
            height: 230, // Legend height
        },
        plotOptions: {
            pie: {
                donut: {
                    size: '70%', // Smaller donut hole (bigger donut)
                    labels: {
                        name: {
                            show: true, // Show the name of the category in the center
                            fontSize: '20px', // Optionally, adjust the font size
                        },
                        value: {
                            show: true, // Show the percentage in the center
                            fontSize: '16px',
                            fontWeight: 'bold',
                            color: '#00FFFF', // Change center label color to #00FFFF
                        },
                    },
                },
            },
        },
    };

    return (
        <div className={styles.piechartContainer} style={{ margin: '0 auto' }}> {/* Increased maxWidth */}
            <ReactApexChartNoSSR options={options} series={values} type="donut" height={400} /> {/* Increased height */}
        </div>
    );
}

export default PieChart;
