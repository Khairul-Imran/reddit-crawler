import { RedditApiResponse } from "../../types/RedditPostData";
import { apiClient } from "./baseApi";

async function getTop20(searchTerm: string, signal?: AbortSignal): Promise<RedditApiResponse> {
    const response = await apiClient<RedditApiResponse>(searchTerm, {
        signal
    });

    console.log("Received response: ", response);

    return {
        data: response.data
    };
}

async function getReport(): Promise<void> {
    const response = await fetch('/api/reports/generate', {
        method: 'GET',
    });

    if (!response.ok) {
        throw new Error('Failed to generate report');
    }

    // Get filename from response headers
    const filename = response.headers
        .get('content-disposition')
        ?.split('filename=')[1]
        ?.replace(/["']/g, '') || 'report.txt';

    // Convert response to blob and download
    const blob = await response.blob();
    const url = window.URL.createObjectURL(blob);
    const a = document.createElement('a');

    a.href = url;
    a.download = filename;
    document.body.appendChild(a);
    a.click();
    window.URL.revokeObjectURL(url);
    a.remove();
}

export const redditDataApi = {
    getTop20,
    getReport
};

/**
 * Notes for self:
 * 
 * Key points:

 * No need for custom hook because:
 * -> One-off operation (download)
 * -> No state management needed
 * -> Simple success/failure outcome
 * -> Browser handles the file download


 * Not using apiClient because:
 * -> File download needs different handling than JSON
 * -> Need to handle blob data
 * -> Need to trigger browser download


 * Error handling is simpler:
 * -> Just success or failure
 * -> No complex data transformation
 * -> Browser handles the actual download
 */
