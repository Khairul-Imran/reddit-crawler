function RedditFooter(): JSX.Element {
    return (
        <footer className="bg-gray-800 text-white w-full py-6 mt-auto">
            <div className="container mx-auto px-4">
                
                <div className="flex flex-col md:flex-row justify-between items-center gap-4">
                    {/* Tech Stack */}
                    <div className="text-sm text-gray-400">
                        Built with: React, TypeScript, and Spring Boot
                    </div>

                    {/* Links */}
                    <div className="flex gap-4">
                        <a
                            href="https://github.com/Khairul-Imran"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-gray-400 hover:text-white transition-colors text-sm"
                        >
                            GitHub Profile
                        </a>
                        <a
                            href="https://github.com/Khairul-Imran/reddit-crawler"
                            target="_blank"
                            rel="noopener noreferrer"
                            className="text-gray-400 hover:text-white transition-colors text-sm"
                        >
                            Project Repository
                        </a>
                    </div>
                </div>

                <div className="mt-4 text-center text-gray-500 text-sm">
                    Powered by Reddit's API
                </div>
            </div>
        </footer>
    );
}

export default RedditFooter;
