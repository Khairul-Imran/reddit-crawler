interface errorMessageProps {
    message: string;
}

function ErrorMessage({ message }: errorMessageProps) {
    return (
        <div className="bg-red-50 border border-red-200 text-red-700 px-4 py-3 rounded-lg">
            <p>{message}</p>
        </div>
    );
}

export default ErrorMessage;
