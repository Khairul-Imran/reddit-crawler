export interface RedditPost {
    postId: string,
    subReddit: string,
    title: string,
    authorName: string,
    upvoteRatio: number,
    upvotes: number,
    score: number,
    totalAwardsReceived: number,
    totalNumberOfComments: number,
    createdTime: number,
    photoUrl: string,
    postUrl: string,
    isVideo: boolean,
    postHint: string,
    domain: string,
    stickied: boolean,
    over18: boolean,
    numberOfCrossposts: number,
    distinguished: string
};

export interface RedditPostData {
    kind: string,  // Will be "t3" for posts
    data: RedditPost
};

export interface RedditListingData {
    after: string,
    dist: number,
    children: RedditPostData[]
}

export interface RedditApiResponse {
    data: RedditListingData
}
