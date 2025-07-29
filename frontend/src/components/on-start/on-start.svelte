<svelte:options runes={true} />
<script lang="ts">
	import InstructionSlides from '$components/instruction-slides/instruction-slides.svelte';
	import { tick } from 'svelte';
	import { browser } from '$app/environment';

	/* function clearStorage() {
		if (!browser) return;
		sessionStorage.removeItem(dayKey);
		localStorage.removeItem(firstTimeKey);
	}
	clearStorage(); */

	// Keys for browser storage
	const firstTimeKey = 'firstTime';
	const dayKey = 'day';

	// Bound elements
	let onStart;
	let introSection;

	let slideDisplay = $state('');
	let animationDone = $state(false);
	const isNewDay = $derived.by(() => {
		if (!browser) return false;
		
		const loggedDay = sessionStorage.getItem(dayKey);
		if (loggedDay) return false;

		const today = new Date();
		const day = today.toLocaleDateString('en-US', {
			year: '2-digit',
			month: '2-digit',
			day: '2-digit'
		});
		sessionStorage.setItem(dayKey, day);
		return true;
	});
	const hideStart = $derived(!isNewDay);
	const firstTime = $derived(browser && !localStorage.getItem(firstTimeKey));
	const hideInstructions = $derived.by(() => {
		if (!browser) return false;
		if (firstTime) return false;
		return true;
	});

	function hideOnStart() {
		localStorage.setItem(firstTimeKey, new Date().toISOString());
		animationDone = false;
		setTimeout(() => {
			onStart.hidden = true;
		}, 300);
	}

	function animationend() {
		if (hideInstructions) {
			hideOnStart();
		} else {
			slideDisplay = 'flex';
			tick().then(() => {
				animationDone = true;
			});
		}
	}

	$effect(() => {
		setTimeout(animationend, 100); // 0.1s
	});

	const slides = [
		`<div class="intro intro-slide">
			Hello, I'm<br />
			<img src="/images/logo/hayli-logo.svg" alt="Hayli" width="360" height="300">
		</div>`,
		`<div class="second-slide intro-slide">
			<img src="/images/speech-bubble.svg" alt="" width="305" height="236">
			<div>I'm so glad you're here!</div>
		</div>`
	];
</script>

<div bind:this={onStart} id="on-start" hidden={hideStart}>
	<div
		bind:this={introSection}
		style:display={slideDisplay}
		id="intro-section"
		hidden={hideInstructions}
		class:hide={!animationDone}
	>
		<InstructionSlides onlyForwards={true} onEnd={hideOnStart} id="start-instructions" {slides} />
	</div>
</div>

<style lang="scss">
	#on-start {
		position: absolute;
		z-index: 3;
		display: flex;
		flex-flow: column;
		justify-content: center;
		--padding: 20px;
		--bottom-padding: 60px;
		padding: var(--padding) var(--padding) var(--bottom-padding);
		height: calc(100vh - var(--padding) - var(--bottom-padding));
		width: calc(100vw - 2 * var(--padding));
		background: #fff;
		text-align: center;
		--animation: 1.5s ease-in-out 2 forwards alternate fade-slide;
		@keyframes -global-fade-slide {
			0% {
				opacity: 0;
			}
			40% {
				opacity: 1;
				transform: translateY(50px);
			}
			100% {
				opacity: 1;
				transform: translateY(50px);
			}
		}

		#intro-section {
			flex: 1 1 auto;
			transition: filter 0.2s ease;
			filter: opacity(1);
			text-align: left;
			&.hide {
				filter: opacity(0);
			}
			:global(.instruction-slides) {
				height: 100%;
			}
			:global(.slide){
				justify-content: center;
			}
			:global(.slides){
				flex: 1 0;
				height: 100%;
			}
			:global(.intro-slide) {
				font: 700 1.5rem var(--ff);
			}
			:global(.intro-slide.intro img){
				display: block;
				width: 100%;
				height: auto;
				margin-top: -40px;
			}
			:global(.second-slide){
				position: relative;
			}
			:global(.second-slide img){
				display: block;
				width: 100%;
				height: auto;
			}
			:global(.second-slide div){
				position: absolute;
				top: 50%;
				left: 50%;
				transform: translate(-50%, -100%);
				width: 100%;
				text-align: center;
			}
		}

		@media (prefers-color-scheme: dark) {
			background: var(--dark2);
		}
		@media (min-width: 1000px){
			--padding: 40px;
			--bottom-padding: 80px;
			padding: var(--padding) var(--padding) var(--bottom-padding);
			width: calc(100vw - 2 * var(--padding));
			#intro-section{
				margin: 0 auto;
				max-width: 1000px;
				width: 100%;
				:global(.intro-slide.intro){
					text-align: center;
				}
				:global(.intro-slide img){
					max-height: 50vh;
				}
			}
		}
	}
</style>
