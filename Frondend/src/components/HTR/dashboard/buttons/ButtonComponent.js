import React from 'react';
import styles from './buttton.module.css'; // Import the CSS module

function ButtonComponent({ data }) {
    const { label, onClickFunc, width, height, fontSize, isMenu, state, setState } = data;

    // Function to toggle the state
    const toggleState = () => {
        // setState(prevState => !prevState); // Toggle the state
        if (onClickFunc) {
            onClickFunc(); // Call the external onClick function if provided
        }
    };

    return (
        <div className={styles['button-container']}>
            <button
                style={{
                    width: `${width}px`,
                    height: `${height}px`,
                    fontSize: `${fontSize}px`,
                }}
                className={`${styles['engine-button']} 
                ${isMenu ? styles.on : state ? styles.on : styles.off}
                `}
                onClick={toggleState}
            >
                {label}
            </button>
        </div>
    );
}

export default ButtonComponent;
