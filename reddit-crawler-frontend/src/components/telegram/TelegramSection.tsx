import { useState } from "react";
import { redditDataApi } from "../../services/api/redditDataApi";
import { config } from "../../config/config";

// Allows parent to pass styling
interface TelegramSectionProps {
    className?: string;
}

function TelegramSection({ className = "" } : TelegramSectionProps) {
    const [isGenerating, setIsGenerating] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleGenerateReport = async () => {
        setIsGenerating(true);
        setError(null);

        try {
            await redditDataApi.getReport();
        } catch (error) {
            console.error("Failed to generate report: ", error);
            setError("Failed to generate report. Please try again.");
        } finally {
            setIsGenerating(false);
        }
    };

    return (
        <div className={`flex items-center gap-4 ${className}`}>
            <button
                onClick={handleGenerateReport}
                disabled={isGenerating}
                className="bg-reddit-orange text-white px-4 py-2 rounded-lg 
                hover:bg-reddit-orange/90 disabled:bg-gray-300
                transition-colors duration-200 flex items-center gap-2"
            >
                {isGenerating ? (
                    <>
                        <span className="animate-spin">⏳</span>
                        Generating Report...
                    </>
                ) : (
                    <>
                        📄 Generate Report
                    </>
                )}
            </button>
            <a
                // href={BOT_URL}
                href={config.telegram.botUrl}
                target="_blank"
                rel="noopener noreferrer"
                className="bg-[#0088cc] text-white px-4 py-2 rounded-lg 
                         hover:bg-[#0088cc]/90 transition-colors duration-200
                         flex items-center gap-2"
            >
                Open in Telegram
            </a>

            {error && <div className="text-red-500 text-sm">{error}</div>}
        </div>
    );
}

export default TelegramSection;
