import CcmLayout from '@/components/ccmLayout';
import React, { useState, useEffect, useRef } from 'react';
import { actions as ccmDynamicActions } from "../../../store/CCM";
import { connect } from "react-redux";
import styles from "./styles.module.css";

const PDFViewer = ({
  ccmUploadPdfFlow,
  ccmViewPdfFlow,
  ccmUploadPdfAPI,
  ccmViewPdfAPI
}) => {
  const [file, setFile] = useState(null);
  const [isUploading, setIsUploading] = useState(false);
  const [error, setError] = useState(null);
  const [pdfUrl, setPdfUrl] = useState(null);
  const [pdfStatus, setPdfStatus] = useState('idle');
  const containerRef = useRef(null);

  const isAdmin = true;

  const getPayloadFromLocalStorage = () => {
    try {
      const storedPayload = localStorage.getItem('payload');
      return storedPayload ? JSON.parse(storedPayload) : {};
    } catch (e) {
      console.error("Error parsing localStorage payload:", e);
      return {};
    }
  };

  const handleUpload = async () => {
    if (!file) {
      setError('Please select a PDF file');
      return;
    }

    setIsUploading(true);
    setError(null);

    try {
      const payload = getPayloadFromLocalStorage();
      const formData = new FormData();

      formData.append('file', file);

      Object.entries(payload).forEach(([key, value]) => {
        formData.append(key, value);
      });

      await ccmUploadPdfAPI(formData);
    } catch (err) {
      setError(err.message || 'Upload failed');
    } finally {
      setIsUploading(false);
    }
  };

  const handleViewPdf = async () => {
    try {
      setPdfStatus('loading');
      setError(null);

      const payload = getPayloadFromLocalStorage();
      await ccmViewPdfAPI(payload);
    } catch (err) {
      setError(err.message || 'Failed to load PDF');
      setPdfStatus('error');
    }
  };

  useEffect(() => {
    if (!ccmViewPdfFlow) return;

    if (pdfUrl) {
      URL.revokeObjectURL(pdfUrl);
      setPdfUrl(null);
    }

    if (ccmViewPdfFlow.data) {
      try {
        const blob = new Blob([ccmViewPdfFlow.data], { type: 'application/pdf' });
        const url = URL.createObjectURL(blob);
        console.log("URL",url)
        setPdfUrl(url);
        setPdfStatus('loaded');
      } catch (err) {
        console.error('Error processing PDF:', err);
        setError('Failed to process PDF');
        setPdfStatus('error');
      }
    } else if (ccmViewPdfFlow.error) {
      setError(ccmViewPdfFlow.error.message || 'Failed to load PDF');
      setPdfStatus('error');
    }
  }, [ccmViewPdfFlow]);

  useEffect(() => {
    if (ccmUploadPdfFlow?.data) {
      handleViewPdf();
    } else if (ccmUploadPdfFlow?.error) {
      setError(ccmUploadPdfFlow.error.message || 'Upload failed');
    }
  }, [ccmUploadPdfFlow]);

  useEffect(() => {
    handleViewPdf();

    return () => {
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
      }
    };
  }, []);

  return (
    <CcmLayout>
      <div className={styles.container}>
        {isAdmin && (
          <div className={styles.uploadSection}>
            <h2>Upload PDF</h2>
            <div className={styles.uploadControls}>
              <input
                type="file"
                accept="application/pdf"
                onChange={(e) => setFile(e.target.files[0])}
                className={styles.fileInput}
              />
              <button
                onClick={handleUpload}
                disabled={!file || isUploading}
                className={styles.uploadButton}
              >
                {isUploading ? 'Uploading...' : 'Upload PDF'}
              </button>
            </div>
            {error && <div className={styles.error}>{error}</div>}
          </div>
        )}

        <div className={styles.viewerSection} ref={containerRef}>
          {pdfStatus === 'loading' && (
            <div className={styles.loading}>Loading PDF...</div>
          )}

          {pdfStatus === 'error' && (
            <div className={styles.errorContainer}>Failed to load PDF.</div>
          )}

          {pdfUrl && (
            <div className={styles.iframeContainer}>
              <iframe
                src={pdfUrl}
                width="100%"
                height="800px"
                title="PDF Document"
                className={styles.iframe}
              />
            </div>
          )}
        </div>
      </div>
    </CcmLayout>
  );
};

const mapStateToProps = (state) => ({
  ccmUploadPdfFlow: state.ccmPatientDetails.ccmUploadPdf,
  ccmViewPdfFlow: state.ccmPatientDetails.ccmViewPdf,
});

const mapDispatchToProps = {
  ccmUploadPdfAPI: ccmDynamicActions.ccmUploadPdfAction,
  ccmViewPdfAPI: ccmDynamicActions.ccmViewPdfAction,
};

export default connect(mapStateToProps, mapDispatchToProps)(PDFViewer);
