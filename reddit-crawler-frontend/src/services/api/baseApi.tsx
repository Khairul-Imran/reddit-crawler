const BASE_URL = '/api/posts';

export class ApiError extends Error {
    constructor( 
        public status: number,
        public message: string,
        public code?: number
    ) {
        super(message);
        this.name = 'ApiError';
    }
}

export async function apiClient<T>(endpoint: string = '', options: RequestInit = {}): Promise<T> {
    const url = `${BASE_URL}/${endpoint}`;
    console.log(`Making API request to: `, url);

    try {
        const response = await fetch(url, {
            ...options,
            headers: {
                'Content-Type': 'application/json',
                ...options.headers,
            }
        });

        // Logging
        console.log('Response status: ', response.status);
        console.log('Response headers: ', response.headers);

        if (!response.ok) {
            const errorData = await response.json();
            console.log('Error response: ', errorData);

            // Handling specific error cases
            if (errorData?.error) {
                throw new ApiError(
                    response.status,
                    errorData.error.message,
                    errorData.error.code
                );
            }

            // Fallback for unexpected error format
            throw new ApiError(
                response.status,
                'An unexpected error occurred.',
                undefined
            );
        }

        // If no error
        const data = await response.json();
        console.log('API response: ', data); // Logging
        return data;

    } catch (error) {
        console.error('API request failed: ', error); // Logging

        // Rethrow Api Error instance
        if (error instanceof ApiError) {
            throw error;
        }

        // Handle network errors
        if (error instanceof TypeError && error.message === "Failed to fetch") {
            throw new ApiError(0, "Unable to connect to the server. Please check your internet connection.", undefined);
        }

        throw new ApiError(500, "An unexpected error occurred.", undefined);
    }
}
