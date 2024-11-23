interface StatusBarProps {
    lastRetrievedTime: Date | null;
}

function StatusBar({ lastRetrievedTime }: StatusBarProps) {
    // Formatting the time
    const formattedTime = (date: Date) => {
        return new Intl.DateTimeFormat("en-US", {
            hour: "numeric",
            minute: "numeric",
            second: "numeric",
            hour12: true,
        }).format(date);
    };

    const getTimeElapsed = (date: Date) => {
        const now = new Date();
        const differenceInSeconds = Math.floor(
            (now.getTime() - date.getTime()) / 1000
        );

        if (differenceInSeconds < 60)
            return `${differenceInSeconds} seconds ago`; // Less than a minute
        if (differenceInSeconds < 3600)
            return `${Math.floor(differenceInSeconds / 60)} minutes ago`; // Less than an hour
        return `${Math.floor(differenceInSeconds / 3600)} hours ago`;
    };

    return (
        <div className="max-w-3xl mx-auto">
            <div className="bg-white shadow-sm rounded-lg p-4 mt-4">
                <div className="flex flex-col sm:flex-row justify-between items-center gap-2">
                    {/* Last Retrieved Time */}
                    <div className="text-sm">
                        {lastRetrievedTime ? (
                            <div className="flex items-center gap-2">
                                <span className="text-gray-500">
                                    Last retrieved:
                                </span>
                                <span className="font-medium text-gray-700">
                                    {formattedTime(lastRetrievedTime)}
                                </span>
                                <span className="text-gray-500">
                                    ({getTimeElapsed(lastRetrievedTime)})
                                </span>
                            </div>
                        ) : (
                            <span className="text-gray-500">
                                Select a subreddit
                            </span>
                        )}
                    </div>

                    <div className="text-xs text-gray-500">
                        {lastRetrievedTime && (
                            <span>
                                Note: Please wait a few minutes between requests
                                to avoid rate limiting
                            </span>
                        )}
                    </div>
                </div>
            </div>
        </div>
    );
}

export default StatusBar;
